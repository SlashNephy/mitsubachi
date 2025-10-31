package blue.starry.mitsubachi.ui.error

import android.content.Context
import blue.starry.mitsubachi.domain.error.AppError
import blue.starry.mitsubachi.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.ui.R
import blue.starry.mitsubachi.ui.snackbar.SnackbarHostService
import blue.starry.mitsubachi.ui.snackbar.enqueue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarErrorPresenter @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val snackbarHostService: SnackbarHostService,
) : ErrorPresenter {
  override suspend fun handle(throwable: Throwable) {
    val message = when (throwable) {
      is AppError -> {
        when (throwable) {
          is UnauthorizedError -> context.getString(R.string.unauthorized_error)
          is NetworkTimeoutError -> context.getString(R.string.network_timeout_error)
        }
      }

      is Exception -> {
        throwable.localizedMessage ?: context.getString(R.string.unknown_error)
      }

      else -> {
        // Error は回復不能とみなして再スロー
        throw throwable
      }
    }

    snackbarHostService.enqueue(message)
  }
}
