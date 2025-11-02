package blue.starry.mitsubachi.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopLocationTrackingUseCase @Inject constructor(
  private val locationTrackingRepository: LocationTrackingRepository,
) {
  suspend operator fun invoke() {
    locationTrackingRepository.stopTracking()
  }
}
