package blue.starry.mitsubachi.ui

import android.content.Context
import blue.starry.mitsubachi.domain.error.AppError
import blue.starry.mitsubachi.domain.error.RequestTimeoutError
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ErrorHandler @Inject constructor(
  @ApplicationContext private val context: Context,
  private val snackbarViewModel: SnackbarViewModel,
) {
  fun handle(error: Throwable) {
    when (error) {
      is RequestTimeoutError -> {
        val message = context.getString(blue.starry.mitsubachi.core.ui.R.string.error_request_timeout)
        snackbarViewModel.enqueue(message)
      }
      is UnauthorizedError -> {
        // UnauthorizedError is handled differently (user is logged out)
        // Don't show a snackbar for this
      }
      is AppError -> {
        // Other AppErrors - show generic message
        snackbarViewModel.enqueue(error.localizedMessage ?: "An error occurred")
      }
      else -> {
        // Non-AppError exceptions
        snackbarViewModel.enqueue(error.localizedMessage ?: "An error occurred")
      }
    }
  }
}
