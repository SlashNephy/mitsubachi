package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.Venue

interface FoursquareApiClient {
  suspend fun getRecentCheckIns(): List<CheckIn>
  suspend fun searchNearVenues(
    latitude: Double,
    longitude: Double,
    query: String? = null,
  ): List<Venue>

  suspend fun addCheckIn(venueId: String, shout: String? = null)
  suspend fun updateCheckIn(checkInId: String, shout: String? = null)
  suspend fun deleteCheckIn(checkInId: String)
}
