package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.cache.ApiResponseCache
import blue.starry.mitsubachi.core.data.cache.CacheKeyGenerator
import blue.starry.mitsubachi.core.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareCheckIn
import blue.starry.mitsubachi.core.data.network.model.FoursquareCheckInResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareSearchVenueRecommendationsResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareSearchVenuesResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserCheckinsResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserPhotosResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserVenueHistoriesResponse
import blue.starry.mitsubachi.core.data.network.model.toDomain
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.model.foursquare.Photo
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import blue.starry.mitsubachi.core.domain.usecase.CachedFoursquareApiClient
import blue.starry.mitsubachi.core.domain.usecase.FoursquareBearerTokenSource
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * Implementation of [CachedFoursquareApiClient] with caching support using encrypted Room database.
 */
@Suppress("TooManyFunctions")
class CachedFoursquareApiClientImpl @Inject constructor(
  private val httpClient: HttpClient,
  private val bearerTokenSource: FoursquareBearerTokenSource,
  private val cache: ApiResponseCache,
) : CachedFoursquareApiClient {
  private val ktorfit = Ktorfit
    .Builder()
    .baseUrl(
      "https://api.foursquare.com/v2",
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

  private companion object {
    const val DEFAULT_USER_ID = "self"
  }

  override suspend fun getRecentCheckIns(
    policy: FetchPolicy,
    limit: Int?,
    after: ZonedDateTime?,
    coordinates: Coordinates?,
  ): List<CheckIn> {
    val cacheKey = CacheKeyGenerator.forRecentCheckIns(
      limit = limit,
      after = after?.toEpochSecond(),
      coordinates = coordinates?.let { "${it.latitude},${it.longitude}" },
    )

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareRecentCheckinsResponse.serializer()),
      networkFetch = {
        ktorfit.getRecentCheckIns(
          limit = limit,
          afterTimeStamp = after?.toEpochSecond(),
          ll = coordinates?.let { "${it.latitude},${it.longitude}" },
        )
      },
    )

    return response.response.recent.map(FoursquareCheckIn::toDomain)
  }

  override suspend fun getCheckIn(
    checkInId: String,
    policy: FetchPolicy,
  ): CheckIn {
    val cacheKey = CacheKeyGenerator.forCheckIn(checkInId)

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareCheckInResponse.serializer()),
      networkFetch = {
        ktorfit.getCheckIn(checkInId = checkInId)
      },
    )

    return response.response.checkIn.toDomain()
  }

  override suspend fun searchNearVenues(
    coordinates: Coordinates,
    query: String?,
    policy: FetchPolicy,
  ): List<Venue> {
    val coordinatesString = "${coordinates.latitude},${coordinates.longitude}"
    val cacheKey = CacheKeyGenerator.forSearchNearVenues(
      coordinates = coordinatesString,
      query = query,
    )

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareSearchVenuesResponse.serializer()),
      networkFetch = {
        ktorfit.searchNearbyVenues(
          ll = coordinatesString,
          query = query?.ifBlank { null },
        )
      },
    )

    return response.response.venues.map { it.toDomain() }
  }

  override suspend fun searchVenueRecommendations(
    coordinates: Coordinates,
    policy: FetchPolicy,
  ): List<VenueRecommendation> {
    val coordinatesString = "${coordinates.latitude},${coordinates.longitude}"
    val cacheKey = CacheKeyGenerator.forSearchVenueRecommendations(
      coordinates = coordinatesString,
    )

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareSearchVenueRecommendationsResponse.serializer()),
      networkFetch = {
        ktorfit.searchVenueRecommendations(
          ll = coordinatesString,
        )
      },
    )

    return response.response.group.results.orEmpty().map { it.toDomain() }
  }

  override suspend fun getUser(
    userId: String?,
    policy: FetchPolicy,
  ): FoursquareUser {
    val effectiveUserId = userId ?: DEFAULT_USER_ID
    val cacheKey = CacheKeyGenerator.forUser(effectiveUserId)

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareUserResponse.serializer()),
      networkFetch = {
        ktorfit.getUser(userId = effectiveUserId)
      },
    )

    return response.response.user.toDomain()
  }

  override suspend fun getUserVenueHistories(
    userId: String?,
    policy: FetchPolicy,
  ): List<VenueHistory> {
    val effectiveUserId = userId ?: DEFAULT_USER_ID
    val cacheKey = CacheKeyGenerator.forUserVenueHistories(effectiveUserId)

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareUserVenueHistoriesResponse.serializer()),
      networkFetch = {
        ktorfit.getUserVenueHistories(userId = effectiveUserId)
      },
    )

    return response.response.venues.items.map { it.toDomain() }
  }

  override suspend fun getUserCheckIns(
    userId: String?,
    limit: Int?,
    offset: Int?,
    policy: FetchPolicy,
  ): List<CheckIn> {
    val effectiveUserId = userId ?: DEFAULT_USER_ID
    val cacheKey = CacheKeyGenerator.forUserCheckIns(
      userId = effectiveUserId,
      limit = limit,
      offset = offset,
    )

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareUserCheckinsResponse.serializer()),
      networkFetch = {
        ktorfit.getUserCheckIns(
          userId = effectiveUserId,
          limit = limit,
          offset = offset,
        )
      },
    )

    return response.response.checkins.items.map(FoursquareCheckIn::toDomain)
  }

  override suspend fun getUserPhotos(
    userId: String?,
    limit: Int?,
    offset: Int?,
    policy: FetchPolicy,
  ): List<Photo> {
    val effectiveUserId = userId ?: DEFAULT_USER_ID
    val cacheKey = CacheKeyGenerator.forUserPhotos(
      userId = effectiveUserId,
      limit = limit,
      offset = offset,
    )

    val response = cache.fetch(
      policy = policy,
      cacheKey = cacheKey,
      serializer = FoursquareApiResponse.serializer(FoursquareUserPhotosResponse.serializer()),
      networkFetch = {
        ktorfit.getUserPhotos(
          userId = effectiveUserId,
          limit = limit,
          offset = offset,
        )
      },
    )

    return response.response.photos.items.map { it.toDomain() }
  }
}
