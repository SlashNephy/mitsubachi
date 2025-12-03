package blue.starry.mitsubachi.feature.checkin.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.usecase.LikeCheckInUseCase
import blue.starry.mitsubachi.core.ui.compose.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.core.ui.compose.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.enqueue
import blue.starry.mitsubachi.feature.checkin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInDetailScreenViewModel @Inject constructor(
  relativeDateTimeFormatter: RelativeDateTimeFormatter,
  @param:ApplicationContext private val context: Context,
  private val likeCheckInUseCase: LikeCheckInUseCase,
  private val snackbarHostService: SnackbarHostService,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
) : ViewModel(), RelativeDateTimeFormatter by relativeDateTimeFormatter {
  fun likeCheckIn(checkInId: String): Job {
    return viewModelScope.launch {
      runCatching {
        likeCheckInUseCase(checkInId)
      }.onException { e ->
        snackbarErrorHandler.handle(e) {
          context.getString(R.string.like_failed, it)
        }
      }
    }
  }

  fun unlikeCheckIn(): Job {
    return viewModelScope.launch {
      snackbarHostService.enqueue(context.getString(R.string.feature_not_implemented))
    }
  }
}
