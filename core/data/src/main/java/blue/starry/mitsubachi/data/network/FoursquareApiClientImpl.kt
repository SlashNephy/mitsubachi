package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareSearchVenuesResponse
import blue.starry.mitsubachi.data.network.model.FoursquareVenue
import blue.starry.mitsubachi.data.network.model.toDomain
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountRepository
import blue.starry.mitsubachi.domain.usecase.FoursquareApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import javax.inject.Inject

class FoursquareApiClientImpl @Inject constructor(
  private val httpClient: HttpClient,
  private val foursquareAccountRepository: FoursquareAccountRepository,
) : FoursquareApiClient {
  private companion object {
    const val API_BASE_URL = "https://api.foursquare.com/v2"
    const val API_VERSION = "20251012"
  }

  override suspend fun getRecentCheckIns(): List<CheckIn> {
    val response = httpClient.get {
      initializeRequest("/checkins/recent")
    }

    val data = response.body<FoursquareApiResponse<FoursquareRecentCheckinsResponse>>()
    return data.response.recent.map(FoursquareRecentCheckinsResponse.Recent::toDomain)
  }

  override suspend fun searchNearVenues(
    latitude: Double,
    longitude: Double,
    query: String?,
  ): List<Venue> {
    val response = httpClient.get {
      initializeRequest("/venues/search")
      url.parameters.append("ll", "$latitude,$longitude")
      if (!query.isNullOrBlank()) {
        url.parameters.append("query", query)
      }
    }

    val data = response.body<FoursquareApiResponse<FoursquareSearchVenuesResponse>>()
    return data.response.venues.map(FoursquareVenue::toDomain)
  }

  override suspend fun addCheckIn(venueId: String, shout: String?) {
    httpClient.post {
      initializeRequest("/checkins/add")
      url.parameters.append("venueId", venueId)
      if (!shout.isNullOrEmpty()) {
        url.parameters.append("shout", shout)
      }
    }
  }

  override suspend fun updateCheckIn(checkInId: String, shout: String?) {
    httpClient.post {
      initializeRequest("/checkins/$checkInId/update")
      if (!shout.isNullOrEmpty()) {
        url.parameters.append("shout", shout)
      }
    }
  }

  override suspend fun deleteCheckIn(checkInId: String) {
    httpClient.post {
      initializeRequest("/checkins/$checkInId/delete")
    }
  }

  private suspend fun HttpRequestBuilder.initializeRequest(path: String) {
    url {
      takeFrom(API_BASE_URL)
      appendPathSegments(path)
      parameters.append("v", API_VERSION)
    }

    val account = foursquareAccountRepository.list().firstOrNull() // TODO: 複数アカウント対応
    if (account == null) {
      throw UnauthorizedError()
    }

    headers.append(HttpHeaders.Authorization, "Bearer ${account.accessToken}")
  }
}
