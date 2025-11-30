package blue.starry.mitsubachi.core.ui.compose.error

import android.content.Context
import blue.starry.mitsubachi.core.domain.error.AppError
import blue.starry.mitsubachi.core.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.core.domain.error.NetworkUnavailableError
import blue.starry.mitsubachi.core.domain.error.UnauthorizedError
import blue.starry.mitsubachi.core.ui.compose.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ErrorFormatterImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : ErrorFormatter {
  override fun format(exception: Exception): String {
    return when (exception) {
      is AppError -> {
        when (exception) {
          is NetworkTimeoutError -> context.getString(R.string.network_timeout_error)
          is NetworkUnavailableError -> context.getString(R.string.network_unavailable_error)
          is UnauthorizedError -> context.getString(R.string.unauthorized_error)
        }
      }

      is Exception -> {
        exception.localizedMessage ?: context.getString(R.string.unknown_error)
      }
    }
  }
}
