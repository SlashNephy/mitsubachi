package blue.starry.mitsubachi.core.data.network.cache

import blue.starry.mitsubachi.core.data.database.dao.CacheDao
import blue.starry.mitsubachi.core.data.database.entity.Cache
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.usecase.FoursquareBearerTokenSource
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.ClientPluginBuilder
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachePlugin @Inject constructor(
  private val cacheDao: CacheDao,
  private val bearerTokenSource: FoursquareBearerTokenSource,
) : KtorClientPluginImpl<Unit>("Cache", {}) {
  private companion object {
    val policyAttribute = AttributeKey<FetchPolicy>("policy")
  }

  @Suppress("LabeledExpression")
  override fun ClientPluginBuilder<Unit>.block() {
    with(client) {
      sendPipeline.intercept(HttpSendPipeline.State) { content ->
        onRequest(this@with, context, content)
      }

      receivePipeline.intercept(HttpReceivePipeline.State) { response ->
        onResponse(response)
      }
    }
  }

  private suspend fun PipelineContext<Any, HttpRequestBuilder>.onRequest(
    client: HttpClient,
    context: HttpRequestBuilder,
    content: Any,
  ) {
    if (!isCacheableRequest(context.method, content)) {
      return
    }

    val policy = context.attributes.getOrNull(policyAttribute) ?: FetchPolicy.NetworkOnly
    val requestUrl = context.url.build()
    Timber.d("fetch policy is $policy: $requestUrl")

    when (policy) {
      FetchPolicy.NetworkOnly -> {
        return
      }

      FetchPolicy.CacheOnly -> {
        val cache = loadCache(requestUrl) ?: error("cache miss") // TODO: AppError にする？
        proceedWithCache(client, cache)
      }

      FetchPolicy.CacheOrNetwork -> {
        val cache = loadCache(requestUrl) ?: return
        proceedWithCache(client, cache)
      }
    }
  }

  private suspend fun loadCache(url: Url): Cache? {
    val cacheKey = CacheKeyGenerator.key(
      getAccountKey(),
      url.toString(),
    )

    Timber.d("loading cache for $url (key = $cacheKey)")

    val now = Instant.now()
    return cacheDao.get(
      key = cacheKey,
      now = now.toEpochMilli(),
    )
  }

  private suspend fun getAccountKey(): String {
    return runCatching {
      // TODO: アクセストークンではなく、アカウントIDにする
      bearerTokenSource.load()
    }.getOrNull().orEmpty()
  }

  @OptIn(InternalAPI::class)
  private suspend fun PipelineContext<Any, HttpRequestBuilder>.proceedWithCache(
    client: HttpClient,
    cache: Cache,
  ) {
    finish()

    val call = HttpClientCall(
      client = client,
      requestData = context.build(),
      responseData = HttpResponseData(
        statusCode = HttpStatusCode.OK,
        requestTime = GMTDate(),
        headers = headersOf(
          HttpHeaders.ContentType,
          ContentType.Application.Json.toString(),
        ),
        version = HttpProtocolVersion.HTTP_1_1,
        body = ByteReadChannel(cache.payload),
        callContext = context.executionContext,
      ),
    )
    proceedWith(call)
  }

  private suspend fun PipelineContext<*, *>.onResponse(response: HttpResponse) {
    if (!isCacheableRequest(response.request.method, response.request.content)) {
      return
    }

    runCatching {
      val cacheKey = CacheKeyGenerator.key(
        getAccountKey(),
        response.request.url.toString(),
      )
      val now = Instant.now()
      cacheDao.insertOrUpdate(
        Cache(
          key = cacheKey,
          payload = response.readRawBytes(),
          createdAt = now,
          expiresAt = now.plus(30, ChronoUnit.DAYS),
        ),
      )
    }
  }

  private fun isCacheableRequest(httpMethod: HttpMethod, content: Any): Boolean {
    return httpMethod == HttpMethod.Get && content is OutgoingContent.NoContent
  }
}
