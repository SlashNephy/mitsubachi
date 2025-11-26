package blue.starry.mitsubachi.feature.map.ui.histories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import blue.starry.mitsubachi.core.domain.usecase.DeviceLocationRepository
import blue.starry.mitsubachi.core.domain.usecase.FetchUserVenueHistoriesUseCase
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.feature.map.ui.toLatLng
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenueHistoriesScreenViewModel @Inject constructor(
  private val fetchUserVenueHistoriesUseCase: FetchUserVenueHistoriesUseCase,
  private val deviceLocationRepository: DeviceLocationRepository,
) : ViewModel() {
  sealed interface UiState {
    data object Loading : UiState
    data class Success(
      val data: List<VenueHistory>,
      val initialPosition: LatLng,
      val isRefreshing: Boolean,
    ) : UiState

    data class Error(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state: StateFlow<UiState> = _state.asStateFlow()

  init {
    refresh()
  }

  fun refresh(): Job {
    return viewModelScope.launch {
      fetch()
    }
  }

  private suspend fun fetch() {
    val currentState = state.value
    if (currentState is UiState.Success) {
      // 2回目以降の更新は isRefreshing=true
      _state.value = currentState.copy(isRefreshing = true)
    } else {
      _state.value = UiState.Loading
    }

    runCatching {
      fetchUserVenueHistoriesUseCase()
    }.onSuccess { data ->
      _state.value =
        UiState.Success(
          data = data,
          initialPosition = data.weightedAveragePosition,
          isRefreshing = false,
        )
    }.onException { e ->
      _state.value = UiState.Error(e)
    }
  }

  // チェックイン回数で重み付けした加重平均のベニューの座標を求める
  private val List<VenueHistory>.weightedAveragePosition: LatLng
    get() {
      val latitudeTotal = sumOf { it.venue.location.latitude * it.count }
      val longitudeTotal = sumOf { it.venue.location.longitude * it.count }
      val weightTotal = sumOf { it.count }

      if (weightTotal == 0) {
        return LatLng(0.0, 0.0)
      }
      return LatLng(latitudeTotal / weightTotal, longitudeTotal / weightTotal)
    }

  suspend fun findCurrentLocation(): LatLng? {
    return try {
      deviceLocationRepository.findCurrentLocation()?.toLatLng()
    } catch (_: SecurityException) {
      // 権限がある前提
      null
    }
  }
}
