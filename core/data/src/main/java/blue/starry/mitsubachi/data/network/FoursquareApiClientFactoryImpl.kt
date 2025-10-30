package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.domain.usecase.FoursquareApiClientFactory
import blue.starry.mitsubachi.domain.usecase.FoursquareBearerTokenSource
import io.ktor.client.HttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoursquareApiClientFactoryImpl @Inject constructor(
  private val httpClient: HttpClient,
) : FoursquareApiClientFactory {
  override fun create(bearerTokenSource: FoursquareBearerTokenSource): FoursquareApiClient {
    return FoursquareApiClientImpl(httpClient, bearerTokenSource)
  }
}
