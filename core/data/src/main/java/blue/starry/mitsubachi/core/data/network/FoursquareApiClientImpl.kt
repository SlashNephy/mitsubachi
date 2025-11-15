package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.model.FoursquareCheckIn
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserVenueHistoriesResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareVenue
import blue.starry.mitsubachi.core.data.network.model.toDomain
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.FilePart
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.model.foursquare.Photo
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.core.domain.usecase.FoursquareBearerTokenSource
import blue.starry.mitsubachi.core.domain.usecase.FoursquareCheckInBroadcastFlag
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.time.ZonedDateTime
import javax.inject.Inject

@Suppress("TooManyFunctions")
class FoursquareApiClientImpl @Inject constructor(
  private val httpClient: HttpClient,
  private val bearerTokenSource: FoursquareBearerTokenSource,
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
              val token = bearerTokenSource.load()
              BearerTokens(token, null)
            }
          }
        }
      },
    )
    .build()
    .createFoursquareNetworkApi()

  override suspend fun getRecentCheckIns(
    limit: Int?,
    after: ZonedDateTime?,
    coordinates: Coordinates?,
  ): List<CheckIn> {
    val data = ktorfit.getRecentCheckIns(
      limit = limit,
      afterTimeStamp = after?.toEpochSecond(),
      ll = coordinates?.let { "${it.latitude},${it.longitude}" },
    )
    return data.response.recent.map(FoursquareCheckIn::toDomain)
  }

  override suspend fun getCheckIn(checkInId: String): CheckIn {
    val data = ktorfit.getCheckIn(checkInId = checkInId)
    return data.response.checkIn.toDomain()
  }

  override suspend fun searchNearVenues(
    coordinates: Coordinates,
    query: String?,
  ): List<Venue> {
    val data = ktorfit.searchNearbyVenues(
      ll = "${coordinates.latitude},${coordinates.longitude}",
      query = query?.ifBlank { null },
    )
    return data.response.venues.map(FoursquareVenue::toDomain)
  }

  override suspend fun searchVenueRecommendations(
    coordinates: Coordinates,
  ): List<VenueRecommendation> {
    val data = ktorfit.searchVenueRecommendations(
      ll = "${coordinates.latitude},${coordinates.longitude}",
    )
    return data.response.group.results.orEmpty().map { it.toDomain() }
  }

  override suspend fun addCheckIn(
    venueId: String,
    shout: String?,
    broadcastFlags: List<FoursquareCheckInBroadcastFlag>?,
    stickerId: String?,
  ): CheckIn {
    val data = ktorfit.addCheckIn(
      venueId = venueId,
      shout = shout?.ifBlank { null },
      broadcast = broadcastFlags?.joinToString(
        ",",
        transform = FoursquareCheckInBroadcastFlag::serialize,
      ),
      stickerId = stickerId?.ifBlank { null },
    )
    return data.response.checkIn.toDomain()
  }

  override suspend fun updateCheckIn(checkInId: String, shout: String?) {
    ktorfit.updateCheckIn(
      checkInId = checkInId,
      shout = shout?.ifBlank { null },
    )
  }

  override suspend fun deleteCheckIn(checkInId: String) {
    ktorfit.deleteCheckIn(checkInId = checkInId)
  }

  override suspend fun getUser(userId: String?): FoursquareUser {
    val data = ktorfit.getUser(userId = userId ?: "self")
    return data.response.user.toDomain()
  }

  override suspend fun getUserVenueHistories(userId: String?): List<VenueHistory> {
    val data = ktorfit.getUserVenueHistories(userId = userId ?: "self")
    return data.response.venues.items.map(FoursquareUserVenueHistoriesResponse.Venues.Item::toDomain)
  }

  override suspend fun addPhotoToCheckIn(
    checkInId: String,
    image: FilePart,
    isPublic: Boolean,
  ) {
    ktorfit.addPhoto(
      checkInId = checkInId,
      public = if (isPublic) 1 else 0,
      body = MultiPartFormDataContent(
        formData {
          append(
            "file",
            image.data,
            Headers.build {
              image.contentType?.also { append(HttpHeaders.ContentType, it) }
              append(HttpHeaders.ContentDisposition, "filename=\"${image.fileName}\"")
            },
          )
        },
      ),
    )
  }

  override suspend fun likeCheckIn(checkInId: String) {
    ktorfit.likeCheckIn(checkInId = checkInId)
  }

  override suspend fun getUserCheckIns(
    userId: String?,
    limit: Int?,
    offset: Int?,
  ): List<CheckIn> {
    val data = ktorfit.getUserCheckIns(
      userId = userId ?: "self",
      limit = limit,
      offset = offset,
    )
    return data.response.checkins.items.map(FoursquareCheckIn::toDomain)
  }

  override suspend fun getUserPhotos(
    userId: String?,
    limit: Int?,
    offset: Int?,
  ): List<Photo> {
    val data = ktorfit.getUserPhotos(
      userId = userId ?: "self",
      limit = limit,
      offset = offset,
    )
    return data.response.photos.items.map { it.toDomain() }
  }
}

private fun FoursquareCheckInBroadcastFlag.serialize(): String {
  return when (this) {
    FoursquareCheckInBroadcastFlag.Public -> "public"
    FoursquareCheckInBroadcastFlag.Private -> "private"
    FoursquareCheckInBroadcastFlag.Facebook -> "facebook"
    FoursquareCheckInBroadcastFlag.Twitter -> "twitter"
  }
}
