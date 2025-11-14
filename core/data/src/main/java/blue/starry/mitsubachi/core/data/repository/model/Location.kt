package blue.starry.mitsubachi.core.data.repository.model

import android.location.Location
import blue.starry.mitsubachi.core.domain.model.DeviceLocation

fun Location.toDomain(): DeviceLocation {
  return DeviceLocation(
    latitude = latitude,
    longitude = longitude,
    altitude = if (hasAltitude()) altitude else null,
    accuracy = if (hasAccuracy()) accuracy else null,
  )
}
