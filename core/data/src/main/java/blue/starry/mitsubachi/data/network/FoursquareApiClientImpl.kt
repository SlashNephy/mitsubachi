package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareVenue
import blue.starry.mitsubachi.data.network.model.toDomain
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountRepository
import blue.starry.mitsubachi.domain.usecase.FoursquareApiClient
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import javax.inject.Inject

class FoursquareApiClientImpl @Inject constructor(
  private val httpClient: HttpClient,
  private val foursquareAccountRepository: FoursquareAccountRepository,
) : FoursquareApiClient {
  private val ktorfit = Ktorfit
    .Builder()
    .baseUrl(
      "https://api.foursquare.com/v2",
      // URL 末尾に / がないとエラーになるが無視する。
      // アノテーションが / 始まりの方が見栄えがいい。
      checkUrl = false,
    )
    .httpClient(
      httpClient.config {
        install(FoursquareApiVersionPlugin)
        install(Auth) {
          bearer {
            loadTokens {
              val account = foursquareAccountRepository.list().firstOrNull() // TODO: 複数アカウント対応
              if (account == null) {
                throw UnauthorizedError()
              }

              BearerTokens(account.accessToken, null)
            }
          }
        }
      },
    )
    .build()
    .createFoursquareNetworkApi()

  override suspend fun getRecentCheckIns(): List<CheckIn> {
    val data = ktorfit.getRecentCheckIns()
    return data.response.recent.map(FoursquareRecentCheckinsResponse.Recent::toDomain)
  }

  override suspend fun searchNearVenues(
    latitude: Double,
    longitude: Double,
    query: String?,
  ): List<Venue> {
    val data = ktorfit.searchNearVenues(
      ll = "$latitude,$longitude",
      query = query?.ifBlank { null },
    )
    return data.response.venues.map(FoursquareVenue::toDomain)
  }

  override suspend fun addCheckIn(venueId: String, shout: String?) {
    ktorfit.addCheckIn(venueId, shout?.ifBlank { null })
  }

  override suspend fun updateCheckIn(checkInId: String, shout: String?) {
    ktorfit.updateCheckIn(checkInId, shout?.ifBlank { null })
  }

  override suspend fun deleteCheckIn(checkInId: String) {
    ktorfit.deleteCheckIn(checkInId)
  }
}
