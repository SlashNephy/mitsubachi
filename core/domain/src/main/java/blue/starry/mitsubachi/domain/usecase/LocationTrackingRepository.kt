package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.LocationTrackingState
import kotlinx.coroutines.flow.StateFlow

interface LocationTrackingRepository {
  val trackingState: StateFlow<LocationTrackingState>

  suspend fun startTracking()
  suspend fun stopTracking()
}
