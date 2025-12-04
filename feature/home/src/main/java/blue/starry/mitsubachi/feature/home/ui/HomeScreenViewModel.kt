package blue.starry.mitsubachi.feature.home.ui

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.usecase.FetchFeedUseCase
import blue.starry.mitsubachi.core.domain.usecase.LikeCheckInUseCase
import blue.starry.mitsubachi.core.ui.compose.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.core.ui.compose.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.enqueue
import blue.starry.mitsubachi.feature.home.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
  relativeDateTimeFormatter: RelativeDateTimeFormatter,
  @param:ApplicationContext private val context: Context,
  private val fetchFeedUseCase: FetchFeedUseCase,
  private val likeCheckInUseCase: LikeCheckInUseCase,
  private val snackbarHostService: SnackbarHostService,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
) : ViewModel(), RelativeDateTimeFormatter by relativeDateTimeFormatter {
  @Immutable
  sealed interface UiState {
    data object Loading : UiState
    data class Success(
      val feed: List<CheckIn>,
      val isRefreshing: Boolean,
      val isLoadingMore: Boolean = false,
      val hasMore: Boolean = true,
    ) : UiState
    data class Error(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

  init {
    refresh()
  }

  fun refresh(): Job {
    return viewModelScope.launch {
      fetch(isLoadingMore = false)
    }
  }

  fun loadMore(): Job {
    return viewModelScope.launch {
      val currentState = state.value
      if (currentState is UiState.Success && !currentState.isLoadingMore && currentState.hasMore) {
        fetch(isLoadingMore = true)
      }
    }
  }

  private suspend fun fetch(isLoadingMore: Boolean) {
    val currentState = state.value
    if (isLoadingMore && currentState is UiState.Success) {
      // 継ぎ足し読み込み
      _state.value = currentState.copy(isLoadingMore = true)

      runCatching {
        val lastTimestamp = currentState.feed.lastOrNull()?.timestamp
        fetchFeedUseCase(limit = 20, after = lastTimestamp)
      }.onSuccess { data ->
        val newFeed = currentState.feed + data
        _state.value = currentState.copy(
          feed = newFeed,
          isLoadingMore = false,
          hasMore = data.isNotEmpty(),
        )
      }.onException { e ->
        _state.value = currentState.copy(isLoadingMore = false)
        snackbarErrorHandler.handle(e) {
          context.getString(R.string.failed_to_load_feed, it)
        }
      }
    } else {
      // 初回読み込みまたはリフレッシュ
      if (currentState is UiState.Success) {
        _state.value = currentState.copy(isRefreshing = true)
      } else {
        _state.value = UiState.Loading
      }

      runCatching {
        fetchFeedUseCase(limit = 20)
      }.onSuccess { data ->
        _state.value = UiState.Success(
          feed = data,
          isRefreshing = false,
          hasMore = data.isNotEmpty(),
        )
      }.onException { e ->
        _state.value = UiState.Error(e)
      }
    }
  }

  fun likeCheckIn(checkInId: String): Job {
    return viewModelScope.launch {
      runCatching {
        likeCheckInUseCase(checkInId)
      }.onSuccess {
        // 楽観的更新
        val currentState = state.value
        if (currentState is UiState.Success) {
          val index = currentState.feed.indexOfFirst { it.id == checkInId }
          if (index != -1) {
            val newCheckIn = currentState.feed[index].copy(isLiked = true)
            val newFeed = currentState.feed.toMutableList()
            newFeed[index] = newCheckIn
            _state.value = currentState.copy(feed = newFeed)
          }
        }
      }.onException { e ->
        snackbarErrorHandler.handle(e) {
          context.getString(R.string.like_failed, it)
        }
      }
    }
  }

  @Suppress("unused")
  fun unlikeCheckIn(checkInId: String) {
    viewModelScope.launch {
      snackbarHostService.enqueue(context.getString(R.string.feature_not_implemented))
    }
  }
}
