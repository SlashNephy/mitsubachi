package blue.starry.mitsubachi.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import blue.starry.mitsubachi.data.repository.model.toDomain
import blue.starry.mitsubachi.domain.model.DeviceLocation
import blue.starry.mitsubachi.domain.usecase.DeviceLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class DeviceLocationRepositoryImpl @Inject constructor(
  private val client: FusedLocationProviderClient,
) : DeviceLocationRepository {
  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
  override suspend fun findLastLocation(timeout: Duration?): DeviceLocation? {
    return withTimeoutOrNullIfNeeded(timeout) {
      client.lastLocation.await()?.toDomain()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
  override suspend fun findCurrentLocation(timeout: Duration?): DeviceLocation? {
    return withTimeoutOrNullIfNeeded(timeout) {
      val cancellationTokenSource = CancellationTokenSource()

      client
        .getCurrentLocation(
          Priority.PRIORITY_BALANCED_POWER_ACCURACY,
          cancellationTokenSource.token,
        )
        .await(cancellationTokenSource)
        ?.toDomain()
    }
  }
}

private suspend fun <T> withTimeoutOrNullIfNeeded(
  timeout: Duration?,
  block: suspend CoroutineScope.() -> T,
): T? {
  return withTimeoutOrNull(timeout ?: Duration.INFINITE, block)
}
