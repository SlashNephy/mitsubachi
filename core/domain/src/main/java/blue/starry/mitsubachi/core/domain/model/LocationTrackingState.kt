package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class LocationTrackingState(
  val isTracking: Boolean,
  val currentLocation: DeviceLocation?,
  val stayDurationMillis: Long,
)
