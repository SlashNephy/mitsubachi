package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.usecase.FetchFeedUseCase
import blue.starry.mitsubachi.domain.usecase.LikeCheckInUseCase
import blue.starry.mitsubachi.ui.AccountEventHandler
import blue.starry.mitsubachi.ui.SnackbarViewModel
import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
  relativeDateTimeFormatter: RelativeDateTimeFormatter,
  private val fetchFeedUseCase: FetchFeedUseCase,
  private val likeCheckInUseCase: LikeCheckInUseCase,
  private val snackbarViewModel: SnackbarViewModel,
) : ViewModel(), AccountEventHandler, RelativeDateTimeFormatter by relativeDateTimeFormatter {
  @Immutable
  sealed interface UiState {
    data object Loading : UiState
    data class Success(val feed: List<CheckIn>, val isRefreshing: Boolean) : UiState
    data class Error(val message: String) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

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
      fetchFeedUseCase()
    }.onSuccess { data ->
      _state.value = UiState.Success(data, isRefreshing = false)
    }.onFailure { e ->
      if (currentState is UiState.Success) {
        // 2回目以降の更新でエラーが起きた場合は、前の成功状態を維持する
        _state.value = currentState.copy(isRefreshing = false)
      } else {
        _state.value = UiState.Error(e.localizedMessage ?: "unknown error")
      }
    }
  }

  override fun onAccountDeleted() {
    _state.value = UiState.Loading
  }

  fun likeCheckIn(checkInId: String): Job {
    return viewModelScope.launch {
      runCatching {
        likeCheckInUseCase(checkInId)
      }.onSuccess {
        // Refresh to get updated like status
        fetch()
      }.onFailure { e ->
        // Log error but don't show error to user
        // The UI will remain in its current state
      }
    }
  }

  fun unlikeCheckIn(checkInId: String) {
    snackbarViewModel.enqueue("この機能は未実装です (⸝⸝›_‹⸝⸝)")
  }
}
