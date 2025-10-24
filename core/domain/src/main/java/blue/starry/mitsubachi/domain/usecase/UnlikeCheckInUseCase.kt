package blue.starry.mitsubachi.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnlikeCheckInUseCase @Inject constructor(
  private val client: FoursquareApiClient,
) {
  suspend operator fun invoke(checkInId: String) {
    return client.unlikeCheckIn(checkInId)
  }
}
