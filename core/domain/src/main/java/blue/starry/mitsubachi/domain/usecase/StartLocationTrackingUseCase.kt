package blue.starry.mitsubachi.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartLocationTrackingUseCase @Inject constructor(
  private val locationTrackingRepository: LocationTrackingRepository,
) {
  suspend operator fun invoke() {
    locationTrackingRepository.startTracking()
  }
}
