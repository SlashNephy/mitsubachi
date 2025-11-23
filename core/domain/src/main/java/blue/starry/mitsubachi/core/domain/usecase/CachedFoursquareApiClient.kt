package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.model.foursquare.Photo
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import java.time.ZonedDateTime

/**
 * Extended API client interface that supports fetch policies for caching.
 *
 * This interface provides the same methods as [FoursquareApiClient] but with
 * an additional [FetchPolicy] parameter to control caching behavior.
 */
@Suppress("TooManyFunctions")
interface CachedFoursquareApiClient {
  suspend fun getRecentCheckIns(
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
    limit: Int? = null,
    after: ZonedDateTime? = null,
    coordinates: Coordinates? = null,
  ): List<CheckIn>

  suspend fun getCheckIn(
    checkInId: String,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): CheckIn

  suspend fun searchNearVenues(
    coordinates: Coordinates,
    query: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): List<Venue>

  suspend fun searchVenueRecommendations(
    coordinates: Coordinates,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): List<VenueRecommendation>

  suspend fun getUser(
    userId: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): FoursquareUser

  suspend fun getUserVenueHistories(
    userId: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): List<VenueHistory>

  suspend fun getUserCheckIns(
    userId: String? = null,
    limit: Int? = null,
    offset: Int? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): List<CheckIn>

  suspend fun getUserPhotos(
    userId: String? = null,
    limit: Int? = null,
    offset: Int? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): List<Photo>
}
