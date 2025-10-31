package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.usecase.FetchFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInDetailScreenViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val fetchFeedUseCase: FetchFeedUseCase,
) : ViewModel() {
  @Immutable
  sealed interface UiState {
    data object Loading : UiState
    data class Success(val checkIn: CheckIn) : UiState
    data class Error(val message: String) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

  private val checkInId: String = checkNotNull(savedStateHandle["id"])

  init {
    loadCheckIn()
  }

  private fun loadCheckIn() {
    viewModelScope.launch {
      runCatching {
        fetchFeedUseCase()
      }.onSuccess { feed ->
        val checkIn = feed.firstOrNull { it.id == checkInId }
        if (checkIn != null) {
          _state.value = UiState.Success(checkIn)
        } else {
          _state.value = UiState.Error("Check-in not found")
        }
      }.onFailure { e ->
        _state.value = UiState.Error(e.localizedMessage ?: "Unknown error")
      }
    }
  }
}
