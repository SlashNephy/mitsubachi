package blue.starry.mitsubachi.ui.error

import android.content.Context
import android.widget.Toast
import blue.starry.mitsubachi.domain.error.AppError
import blue.starry.mitsubachi.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastErrorPresenter @Inject constructor(
  @param:ApplicationContext private val context: Context,
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

    withContext(Dispatchers.Main) {
      Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
  }
}
