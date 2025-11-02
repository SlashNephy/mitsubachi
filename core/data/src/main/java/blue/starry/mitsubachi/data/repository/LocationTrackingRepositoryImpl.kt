package blue.starry.mitsubachi.data.repository

import android.content.Context
import blue.starry.mitsubachi.data.service.LocationTrackingService
import blue.starry.mitsubachi.domain.model.LocationTrackingState
import blue.starry.mitsubachi.domain.usecase.LocationTrackingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationTrackingRepositoryImpl @Inject constructor(
  @ApplicationContext private val context: Context,
) : LocationTrackingRepository {
  private val _trackingState = MutableStateFlow(
    LocationTrackingState(
      isTracking = false,
      currentLocation = null,
      stayDurationMillis = 0L,
    ),
  )
  override val trackingState: StateFlow<LocationTrackingState> = _trackingState.asStateFlow()

  override suspend fun startTracking() {
    LocationTrackingService.startTracking(context)
    _trackingState.value = _trackingState.value.copy(isTracking = true)
  }

  override suspend fun stopTracking() {
    LocationTrackingService.stopTracking(context)
    _trackingState.value = LocationTrackingState(
      isTracking = false,
      currentLocation = null,
      stayDurationMillis = 0L,
    )
  }
}
