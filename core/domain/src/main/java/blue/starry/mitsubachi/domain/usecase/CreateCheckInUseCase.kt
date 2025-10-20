package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.Venue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateCheckInUseCase @Inject constructor(
  val client: FoursquareApiClient,
) {
  suspend operator fun invoke(venue: Venue, shout: String?) {
    client.addCheckIn(venueId = venue.id, shout = shout)
  }
}
