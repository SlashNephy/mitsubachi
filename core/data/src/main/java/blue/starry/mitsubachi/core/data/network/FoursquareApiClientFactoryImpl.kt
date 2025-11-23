package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.cache.ApiResponseCache
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClientFactory
import blue.starry.mitsubachi.core.domain.usecase.FoursquareBearerTokenSource
import io.ktor.client.HttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoursquareApiClientFactoryImpl @Inject constructor(
  private val httpClient: HttpClient,
  private val cache: ApiResponseCache,
) : FoursquareApiClientFactory {
  override fun create(bearerTokenSource: FoursquareBearerTokenSource): FoursquareApiClient {
    return FoursquareApiClientImpl(httpClient, bearerTokenSource, cache)
  }
}
