package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Venue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateCheckInUseCase @Inject constructor(
  val client: FoursquareApiClient,
) {
  suspend operator fun invoke(venue: Venue, shout: String?, isPublic: Boolean?): CheckIn {
    return client.addCheckIn(
      venueId = venue.id,
      shout = shout,
      broadcastFlags = isPublic.toBroadcastFlags(),
    )
  }

  private fun Boolean?.toBroadcastFlags(): List<FoursquareCheckInBroadcastFlag>? {
    return when (this) {
      true -> listOf(FoursquareCheckInBroadcastFlag.Public)
      false -> listOf(FoursquareCheckInBroadcastFlag.Private)
      null -> null
    }
  }
}
