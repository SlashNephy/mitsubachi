package blue.starry.mitsubachi.feature.map.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.usecase.DeviceLocationRepository
import blue.starry.mitsubachi.core.domain.usecase.SearchVenueRecommendationsUseCase
import blue.starry.mitsubachi.core.ui.compose.error.onException
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SearchMapScreenViewModel @Inject constructor(
  private val searchVenueRecommendationsUseCase: SearchVenueRecommendationsUseCase,
  private val deviceLocationRepository: DeviceLocationRepository,
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

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedSection = MutableStateFlow<String?>(null)
  val selectedSection: StateFlow<String?> = _selectedSection.asStateFlow()

  private val _currentLocation = MutableStateFlow<LatLng?>(null)
  val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

  private var lastLocation: LatLng? = null

  suspend fun loadCurrentLocation() {
    @Suppress("TooGenericExceptionCaught")
    try {
      val location = deviceLocationRepository.findCurrentLocation(timeout = 5.seconds)
      if (location != null) {
        val userLocation = LatLng(location.latitude, location.longitude)
        _currentLocation.value = userLocation
      }
    } catch (_: Exception) {
      // 位置情報の取得に失敗した場合は何もしない
      // 可能性のあるエラー:
      // - タイムアウト
      // - 位置情報が無効
      // - 権限エラー
      // いずれの場合もデフォルト位置（東京駅）が使用される
    }
  }

  fun updateCurrentLocation(location: LatLng) {
    lastLocation = location
    refreshVenues()
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
    lastLocation?.let { refreshVenues() }
  }

  fun selectSection(section: String?) {
    _selectedSection.value = section
    lastLocation?.let { refreshVenues() }
  }

  private fun refreshVenues() {
    val location = lastLocation ?: return
    viewModelScope.launch {
      val currentState = state.value
      _state.value = UiState.Loading

      runCatching {
        val coordinates = Coordinates(
          latitude = location.latitude,
          longitude = location.longitude,
        )
        searchVenueRecommendationsUseCase(
          coordinates = coordinates,
          query = _searchQuery.value.ifBlank { null },
          section = _selectedSection.value,
        )
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
