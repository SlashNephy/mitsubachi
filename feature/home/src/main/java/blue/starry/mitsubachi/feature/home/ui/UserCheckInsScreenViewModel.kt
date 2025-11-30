package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.usecase.FetchUserCheckInsUseCase
import blue.starry.mitsubachi.core.domain.usecase.LikeCheckInUseCase
import blue.starry.mitsubachi.core.ui.compose.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.core.ui.compose.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.enqueue
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
) : ViewModel(), RelativeDateTimeFormatter by relativeDateTimeFormatter {
  @Immutable
  sealed interface UiState {
    data object Loading : UiState
    data class Success(val checkIns: List<CheckIn>, val isRefreshing: Boolean) : UiState
    data class Error(val exception: Exception) : UiState
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
    }.onException { e ->
      _state.value = UiState.Error(e)
    }
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
      }.onException { e ->
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
