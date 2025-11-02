package blue.starry.mitsubachi.ui.feature.map.histories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.foursquare.VenueHistory
import blue.starry.mitsubachi.domain.usecase.FetchUserVenueHistoriesUseCase
import blue.starry.mitsubachi.ui.error.ErrorFormatter
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
  private val errorFormatter: ErrorFormatter,
) : ViewModel() {
  sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<VenueHistory>, val isRefreshing: Boolean) : UiState
    data class Error(val message: String) : UiState
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
      _state.value = UiState.Success(data, isRefreshing = false)
    }.onFailure { e ->
      if (currentState is UiState.Success) {
        // 2回目以降の更新でエラーが起きた場合は、前の成功状態を維持する
        _state.value = currentState.copy(isRefreshing = false)
      } else {
        _state.value = UiState.Error(errorFormatter.format(e))
      }
    }
  }
}

// チェックイン回数で重み付けした加重平均のベニューの座標を求める
val VenueHistoriesScreenViewModel.UiState.Success.weightedAveragePosition: LatLng
  get() {
    val latitudeTotal = data.sumOf { it.venue.location.latitude * it.count }
    val longitudeTotal = data.sumOf { it.venue.location.longitude * it.count }
    val weightTotal = data.sumOf { it.count }

    if (weightTotal == 0) {
      return LatLng(0.0, 0.0)
    }
    return LatLng(latitudeTotal / weightTotal, longitudeTotal / weightTotal)
  }
