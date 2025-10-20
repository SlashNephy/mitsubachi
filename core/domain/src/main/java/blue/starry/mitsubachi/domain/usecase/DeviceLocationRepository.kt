package blue.starry.mitsubachi.domain.usecase

import android.Manifest
import androidx.annotation.RequiresPermission
import blue.starry.mitsubachi.domain.model.DeviceLocation
import kotlin.time.Duration

interface DeviceLocationRepository {
  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
  suspend fun findLastLocation(timeout: Duration? = null): DeviceLocation?

  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
  suspend fun findCurrentLocation(timeout: Duration? = null): DeviceLocation?
}
