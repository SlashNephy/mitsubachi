package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.Coordinates
import blue.starry.mitsubachi.domain.model.FilePart
import blue.starry.mitsubachi.domain.model.FoursquareUser
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.model.VenueRecommendation
import blue.starry.mitsubachi.domain.model.foursquare.Photo
import blue.starry.mitsubachi.domain.model.foursquare.VenueHistory
import java.time.ZonedDateTime

@Suppress("TooManyFunctions")
interface FoursquareApiClient {
  suspend fun getRecentCheckIns(
    limit: Int? = null,
    after: ZonedDateTime? = null,
    coordinates: Coordinates? = null,
  ): List<CheckIn>

  suspend fun searchNearVenues(
    coordinates: Coordinates,
    query: String? = null,
  ): List<Venue>

  suspend fun searchVenueRecommendations(
    coordinates: Coordinates,
  ): List<VenueRecommendation>

  suspend fun addCheckIn(
    venueId: String,
    shout: String? = null,
    broadcastFlags: List<FoursquareCheckInBroadcastFlag>? = null,
    stickerId: String? = null,
  ): CheckIn

  suspend fun updateCheckIn(checkInId: String, shout: String? = null)
  suspend fun deleteCheckIn(checkInId: String)
  suspend fun getUser(userId: String? = null): FoursquareUser
  suspend fun getUserVenueHistories(userId: String? = null): List<VenueHistory>
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
  ): List<CheckIn>

  suspend fun getUserPhotos(
    userId: String? = null,
    limit: Int? = null,
    offset: Int? = null,
  ): List<Photo>
}
