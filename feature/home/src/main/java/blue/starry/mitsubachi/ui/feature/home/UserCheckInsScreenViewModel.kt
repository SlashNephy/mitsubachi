package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.usecase.FetchUserCheckInsUseCase
import blue.starry.mitsubachi.domain.usecase.LikeCheckInUseCase
import blue.starry.mitsubachi.ui.AccountEventHandler
import blue.starry.mitsubachi.ui.error.ErrorFormatter
import blue.starry.mitsubachi.ui.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.ui.snackbar.SnackbarHostService
import blue.starry.mitsubachi.ui.snackbar.enqueue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserCheckInsScreenViewModel @Inject constructor(
  relativeDateTimeFormatter: RelativeDateTimeFormatter,
  private val fetchUserCheckInsUseCase: FetchUserCheckInsUseCase,
  private val likeCheckInUseCase: LikeCheckInUseCase,
  private val snackbarHostService: SnackbarHostService,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
  private val errorFormatter: ErrorFormatter,
) : ViewModel(), AccountEventHandler, RelativeDateTimeFormatter by relativeDateTimeFormatter {
  @Immutable
  sealed interface UiState {
    data object Loading : UiState
    data class Success(val checkIns: List<CheckIn>, val isRefreshing: Boolean) : UiState
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
      _state.value = currentState.copy(isRefreshing = true)
    } else {
      _state.value = UiState.Loading
    }

    runCatching {
      fetchUserCheckInsUseCase()
    }.onSuccess { data ->
      _state.value = UiState.Success(data, isRefreshing = false)
    }.onFailure { e ->
      snackbarErrorHandler.handle(e)
      if (currentState is UiState.Success) {
        _state.value = currentState.copy(isRefreshing = false)
      } else {
        _state.value = UiState.Error(errorFormatter.format(e))
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
        val currentState = state.value
        if (currentState is UiState.Success) {
          val index = currentState.checkIns.indexOfFirst { it.id == checkInId }
          if (index != -1) {
            val oldCheckIn = currentState.checkIns[index]
            val newCheckIn = oldCheckIn.copy(
              isLiked = true,
              likeCount = oldCheckIn.likeCount + 1,
            )
            val newCheckIns = currentState.checkIns.toMutableList()
            newCheckIns[index] = newCheckIn
            _state.value = currentState.copy(checkIns = newCheckIns)
          }
        }
      }.onFailure { e ->
        snackbarErrorHandler.handle(e) {
          "いいねに失敗しました: $it"
        }
      }
    }
  }

  @Suppress("unused")
  fun unlikeCheckIn(checkInId: String) {
    viewModelScope.launch {
      snackbarHostService.enqueue("この機能は未実装です (⸝⸝›_‹⸝⸝)")
    }
  }
}
