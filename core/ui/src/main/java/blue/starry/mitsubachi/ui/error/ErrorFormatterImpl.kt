package blue.starry.mitsubachi.ui.error

import android.content.Context
import blue.starry.mitsubachi.domain.error.AppError
import blue.starry.mitsubachi.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.ui.R
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorFormatterImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : ErrorFormatter {
  override fun format(throwable: Throwable): String {
    return when (throwable) {
      is AppError -> {
        when (throwable) {
          is UnauthorizedError -> context.getString(R.string.unauthorized_error)
          is NetworkTimeoutError -> context.getString(R.string.network_timeout_error)
        }
      }

      is Exception -> {
        // 未知の例外だったら Firebase Crashlytics に報告
        Firebase.crashlytics.recordException(throwable)

        throwable.localizedMessage ?: context.getString(R.string.unknown_error)
      }

      else -> {
        // Error は回復不能とみなして再スロー
        throw throwable
      }
    }
  }
}
