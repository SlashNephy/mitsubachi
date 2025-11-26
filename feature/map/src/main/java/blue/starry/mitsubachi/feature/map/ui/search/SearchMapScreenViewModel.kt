package blue.starry.mitsubachi.feature.map.ui.search

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import blue.starry.mitsubachi.core.domain.usecase.DeviceLocationRepository
import blue.starry.mitsubachi.core.domain.usecase.SearchVenueRecommendationsUseCase
import blue.starry.mitsubachi.core.domain.usecase.VenueRecommendationSection
import blue.starry.mitsubachi.core.ui.compose.error.onException
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SearchMapScreenViewModel @Inject constructor(
  @param:ApplicationContext private val context: Context,
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

  private val _selectedSection = MutableStateFlow<VenueRecommendationSection?>(null)
  val selectedSection: StateFlow<VenueRecommendationSection?> = _selectedSection.asStateFlow()

  private val _currentLocation = MutableStateFlow<LatLng?>(null)
  val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

  private var lastLocation: LatLng? = null

  suspend fun loadCurrentLocation() {
    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }

    runCatching {
      deviceLocationRepository.findCurrentLocation(timeout = 5.seconds)
    }.onSuccess { location ->
      location?.also {
        _currentLocation.value = LatLng(it.latitude, it.longitude)
      }
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

  fun selectSection(section: VenueRecommendationSection?) {
    _selectedSection.value = section
    lastLocation?.let { refreshVenues() }
  }

  private fun refreshVenues() {
    val location = lastLocation ?: return
    viewModelScope.launch {
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
        _state.value = UiState.Error(e, location)
      }
    }
  }

  fun selectVenue(venue: VenueRecommendation?) {
    _selectedVenue.value = venue
  }
}
