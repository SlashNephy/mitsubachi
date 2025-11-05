package blue.starry.mitsubachi.ui.error

import blue.starry.mitsubachi.domain.error.AppError
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ErrorReporterImpl @Inject constructor() : ErrorReporter {
  override fun report(throwable: Throwable) {
    when (throwable) {
      is AppError -> {
        Timber.d(throwable, "AppError reported")

        // AppError はハンドリングする前提なので Firebase Crashlytics に記録しない
      }

      is Exception -> {
        Timber.e(throwable, "unknown exception reported")

        // 未知の例外だったら Firebase Crashlytics に報告
        Firebase.crashlytics.recordException(throwable)
      }
    }
  }
}
