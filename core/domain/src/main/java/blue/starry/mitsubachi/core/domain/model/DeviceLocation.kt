package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceLocation(
  val latitude: Double,
  val longitude: Double,
  val altitude: Double?,
  val accuracy: Float?,
)
