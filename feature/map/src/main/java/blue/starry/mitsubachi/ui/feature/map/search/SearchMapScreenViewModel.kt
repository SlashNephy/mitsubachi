package blue.starry.mitsubachi.ui.feature.map.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.Coordinates
import blue.starry.mitsubachi.domain.model.VenueRecommendation
import blue.starry.mitsubachi.domain.usecase.SearchVenueRecommendationsUseCase
import blue.starry.mitsubachi.ui.error.onException
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchMapScreenViewModel @Inject constructor(
  private val searchVenueRecommendationsUseCase: SearchVenueRecommendationsUseCase,
) : ViewModel() {
  sealed interface UiState {
    data object Loading : UiState
    data class Success(
      val venueRecommendations: List<VenueRecommendation>,
      val isBottomSheetVisible: Boolean,
    ) : UiState

    data class Error(val exception: Exception, val lastLocation: LatLng) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state: StateFlow<UiState> = _state.asStateFlow()

  private val _selectedVenue = MutableStateFlow<VenueRecommendation?>(null)
  val selectedVenue: StateFlow<VenueRecommendation?> = _selectedVenue.asStateFlow()

  fun updateCurrentLocation(location: LatLng) {
    viewModelScope.launch {
      val currentState = state.value
      _state.value = UiState.Loading

      runCatching {
        val coordinates = Coordinates(
          latitude = location.latitude,
          longitude = location.longitude,
        )
        searchVenueRecommendationsUseCase(coordinates)
      }.onSuccess { venues ->
        _state.value = UiState.Success(
          venueRecommendations = venues,
          isBottomSheetVisible = true,
        )
      }.onException { e ->
        if (currentState is UiState.Success) {
          // 2回目以降の更新でエラーが起きた場合は、前の成功状態を維持する
          _state.value = currentState
        } else {
          _state.value = UiState.Error(e, location)
        }
      }
    }
  }

  fun selectVenue(venue: VenueRecommendation?) {
    _selectedVenue.value = venue
  }
}
