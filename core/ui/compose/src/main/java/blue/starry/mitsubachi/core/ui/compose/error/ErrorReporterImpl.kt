package blue.starry.mitsubachi.core.ui.compose.error

import blue.starry.mitsubachi.core.domain.error.AppError
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ErrorReporterImpl @Inject constructor() : ErrorReporter {
  override fun report(exception: Exception) {
    when (exception) {
      is AppError -> {
        Timber.d(exception, "AppError reported")

        // AppError はハンドリングする前提なので Firebase Crashlytics に記録しない
      }

      is Exception -> {
        Timber.e(exception, "unknown exception reported")

        // 未知の例外だったら Firebase Crashlytics に報告
        Firebase.crashlytics.recordException(exception)
      }
    }
  }
}
