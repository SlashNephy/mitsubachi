package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.FilePart
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.model.foursquare.Photo
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import java.time.ZonedDateTime

@Suppress("TooManyFunctions")
interface FoursquareApiClient {
  suspend fun getRecentCheckIns(
    limit: Int? = null,
    after: ZonedDateTime? = null,
    coordinates: Coordinates? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
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

  suspend fun addCheckIn(
    venueId: String,
    shout: String? = null,
    broadcastFlags: List<FoursquareCheckInBroadcastFlag>? = null,
    stickerId: String? = null,
  ): CheckIn

  suspend fun updateCheckIn(checkInId: String, shout: String? = null)
  suspend fun deleteCheckIn(checkInId: String)
  suspend fun getUser(
    userId: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): FoursquareUser

  suspend fun getUserVenueHistories(
    userId: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): List<VenueHistory>

  suspend fun addPhotoToCheckIn(
    checkInId: String,
    image: FilePart,
    isPublic: Boolean = true,
  )

  suspend fun likeCheckIn(checkInId: String)
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
