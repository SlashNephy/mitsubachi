package blue.starry.mitsubachi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
  sealed interface UiState {
    data object Initializing : UiState
    data object Ready : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Initializing)
  val state = _state.asStateFlow()

  fun onReady() {
    _state.value = UiState.Ready
  }
}
