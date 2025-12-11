package blue.starry.mitsubachi.feature.home.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.usecase.FetchFeedUseCase
import blue.starry.mitsubachi.core.domain.usecase.LikeCheckInUseCase
import blue.starry.mitsubachi.core.ui.compose.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.core.ui.compose.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.enqueue
import blue.starry.mitsubachi.feature.home.R
import blue.starry.mitsubachi.feature.home.paging.HomeFeedPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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
  private val likedCheckInIds = MutableStateFlow<Set<String>>(emptySet())

  val pagingDataFlow: Flow<PagingData<CheckIn>> = Pager(
    config = PagingConfig(
      pageSize = PAGE_SIZE,
      enablePlaceholders = false,
      initialLoadSize = PAGE_SIZE,
    ),
    pagingSourceFactory = {
      HomeFeedPagingSource(fetchFeedUseCase)
    },
  ).flow
    .map { pagingData ->
      // Apply optimistic like updates
      pagingData.map { checkIn ->
        if (likedCheckInIds.value.contains(checkIn.id)) {
          checkIn.copy(isLiked = true)
        } else {
          checkIn
        }
      }
    }
    .cachedIn(viewModelScope)

  fun likeCheckIn(checkInId: String): Job {
    return viewModelScope.launch {
      // Optimistic update
      likedCheckInIds.value = likedCheckInIds.value + checkInId

      runCatching {
        likeCheckInUseCase(checkInId)
      }.onException { e ->
        // Revert on error
        likedCheckInIds.value = likedCheckInIds.value - checkInId
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

  private companion object {
    const val PAGE_SIZE = 20
  }
}
