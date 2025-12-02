package blue.starry.mitsubachi.core.ui.compose.error

import kotlinx.coroutines.CancellationException

inline fun <T> Result<T>.onException(action: (exception: Exception) -> Unit): Result<T> {
  return apply {
    when (val exception = exceptionOrNull()) {
      null -> {}

      // 非 Exception は回復不能とみなして再スロー
      !is Exception -> {
        throw exception
      }

      is CancellationException -> {
        throw exception
      }

      else -> {
        action(exception)
      }
    }
  }
}
