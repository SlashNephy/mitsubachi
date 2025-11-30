package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.LocationTrackingState
import kotlinx.coroutines.flow.StateFlow

interface LocationTrackingRepository {
  val trackingState: StateFlow<LocationTrackingState>

  suspend fun startTracking()
  suspend fun stopTracking()
}
