package blue.starry.mitsubachi.feature.checkin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.core.ui.compose.error.onException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInDetailLoadingScreenViewModel @Inject constructor(
  private val foursquare: FoursquareApiClient,
) : ViewModel() {
  sealed interface UiState {
    data object Loading : UiState
    data class Loaded(val checkIn: CheckIn) : UiState
    data class Error(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

  fun fetchCheckIn(id: String): Job {
    return viewModelScope.launch {
      _state.value = UiState.Loading

      runCatching {
        foursquare.getCheckIn(id)
      }.onSuccess { checkIn ->
        _state.value = UiState.Loaded(checkIn)
      }.onException { e ->
        _state.value = UiState.Error(e)
      }
    }
  }
}
