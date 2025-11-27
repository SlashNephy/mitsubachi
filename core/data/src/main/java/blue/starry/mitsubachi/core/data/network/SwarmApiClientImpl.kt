package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.cache.CachePlugin
import blue.starry.mitsubachi.core.data.network.model.toDomain
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.usecase.SwarmApiClient
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import org.apache.commons.lang3.RandomStringUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Singleton
class SwarmApiClientImpl @Inject constructor(
  httpClient: HttpClient,
  cachePlugin: CachePlugin,
) : SwarmApiClient {
  private val ktorfit = Ktorfit
    .Builder()
    .baseUrl("https://api.foursquare.com/v2", checkUrl = false)
    .httpClient(
      httpClient.config {
        install(UserAgent) {
          // KtorClientModule で設定されている値をリセット
          agent = ""
        }
        install(cachePlugin)
      },
    )
    .build()
    .createSwarmNetworkApi()

  @OptIn(ExperimentalUuidApi::class)
  override suspend fun getRecentActivities(
    oauthToken: String,
    uniqueDevice: String?,
    wsid: String?,
    userAgent: String?,
    policy: FetchPolicy,
  ): List<CheckIn> {
    val data = ktorfit.getRecentActivities(
      uniqueDevice = uniqueDevice ?: RandomStringUtils.secure().nextHex(24),
      oauthToken = oauthToken,
      wsid = wsid ?: Uuid.random().toHexDashString(),
      userAgent = userAgent ?: "com.foursquare.robin:2025081819:20220328:16:Pixel 10:release",
      policy = policy,
    )
    return data.response.activities.items.map { it.checkin.toDomain() }
  }

  private fun RandomStringUtils.nextHex(count: Int): String {
    return next(count, "0123456789abcdef")
  }
}
