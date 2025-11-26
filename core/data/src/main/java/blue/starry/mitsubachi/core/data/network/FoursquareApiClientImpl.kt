package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.cache.CachePlugin
import blue.starry.mitsubachi.core.data.network.model.FoursquareCheckIn
import blue.starry.mitsubachi.core.data.network.model.toDomain
import blue.starry.mitsubachi.core.data.network.model.toQuery
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.FilePart
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.model.foursquare.Photo
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.core.domain.usecase.FoursquareBearerTokenSource
import blue.starry.mitsubachi.core.domain.usecase.FoursquareCheckInBroadcastFlag
import blue.starry.mitsubachi.core.domain.usecase.VenueRecommendationSection
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
  cachePlugin: CachePlugin,
) : FoursquareApiClient {
  private companion object {
    const val SELF_USER_ID = "self"
  }

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
        install(cachePlugin)
      },
    )
    .build()
    .createFoursquareNetworkApi()

  override suspend fun getRecentCheckIns(
    limit: Int?,
    after: ZonedDateTime?,
    coordinates: Coordinates?,
    policy: FetchPolicy,
  ): List<CheckIn> {
    val data = ktorfit.getRecentCheckIns(
      limit = limit,
      afterTimeStamp = after?.toEpochSecond(),
      ll = coordinates?.let { "${it.latitude},${it.longitude}" },
      policy = policy,
    )
    return data.response.recent.map(FoursquareCheckIn::toDomain)
  }

  override suspend fun getCheckIn(
    checkInId: String,
    policy: FetchPolicy,
  ): CheckIn {
    val data = ktorfit.getCheckIn(checkInId = checkInId, policy = policy)
    return data.response.checkIn.toDomain()
  }

  override suspend fun searchNearVenues(
    coordinates: Coordinates,
    query: String?,
    near: String?,
    radius: Int?,
    categoryId: String?,
    limit: Int?,
    url: String?,
    policy: FetchPolicy,
  ): List<Venue> {
    val data = ktorfit.searchNearbyVenues(
      query = query?.ifBlank { null },
      ll = "${coordinates.latitude},${coordinates.longitude}",
      near = near?.ifBlank { null },
      radius = radius,
      categoryId = categoryId?.ifBlank { null },
      limit = limit,
      url = url?.ifBlank { null },
      policy = policy,
    )
    return data.response.venues.map { it.toDomain() }
  }

  override suspend fun searchVenueRecommendations(
    coordinates: Coordinates,
    query: String?,
    radius: Int?,
    sw: String?,
    ne: String?,
    near: String?,
    section: VenueRecommendationSection?,
    categoryId: String?,
    novelty: String?,
    friendVisits: String?,
    time: String?,
    day: String?,
    lastVenue: String?,
    openNow: Boolean?,
    price: String?,
    saved: Boolean?,
    sortByDistance: Boolean?,
    sortByPopularity: Boolean?,
    limit: Int?,
    offset: Int?,
    policy: FetchPolicy,
  ): List<VenueRecommendation> {
    val data = ktorfit.searchVenueRecommendations(
      ll = "${coordinates.latitude},${coordinates.longitude}",
      query = query,
      radius = radius,
      sw = sw?.ifBlank { null },
      ne = ne?.ifBlank { null },
      near = near?.ifBlank { null },
      section = section?.toQuery(),
      categoryId = categoryId?.ifBlank { null },
      novelty = novelty?.ifBlank { null },
      friendVisits = friendVisits?.ifBlank { null },
      time = time?.ifBlank { null },
      day = day?.ifBlank { null },
      lastVenue = lastVenue?.ifBlank { null },
      openNow = openNow,
      price = price?.ifBlank { null },
      saved = saved,
      sortByDistance = sortByDistance,
      sortByPopularity = sortByPopularity,
      limit = limit,
      offset = offset,
      policy = policy,
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
        transform = FoursquareCheckInBroadcastFlag::toQuery,
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

  override suspend fun getUser(
    userId: String?,
    policy: FetchPolicy,
  ): FoursquareUser {
    val data = ktorfit.getUser(userId = userId ?: SELF_USER_ID, policy = policy)
    return data.response.user.toDomain()
  }

  override suspend fun getUserVenueHistories(
    userId: String?,
    policy: FetchPolicy,
  ): List<VenueHistory> {
    val data = ktorfit.getUserVenueHistories(userId = userId ?: SELF_USER_ID, policy = policy)
    return data.response.venues.items.map { it.toDomain() }
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
    policy: FetchPolicy,
  ): List<CheckIn> {
    val data = ktorfit.getUserCheckIns(
      userId = userId ?: SELF_USER_ID,
      limit = limit,
      offset = offset,
      policy = policy,
    )
    return data.response.checkins.items.map(FoursquareCheckIn::toDomain)
  }

  override suspend fun getUserPhotos(
    userId: String?,
    limit: Int?,
    offset: Int?,
    policy: FetchPolicy,
  ): List<Photo> {
    val data = ktorfit.getUserPhotos(
      userId = userId ?: SELF_USER_ID,
      limit = limit,
      offset = offset,
      policy = policy,
    )
    return data.response.photos.items.map { it.toDomain() }
  }
}
