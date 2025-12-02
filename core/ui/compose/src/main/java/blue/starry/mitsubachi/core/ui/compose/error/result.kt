package blue.starry.mitsubachi.core.ui.compose.error

import kotlinx.coroutines.CancellationException

inline fun <T> Result<T>.onException(action: (exception: Exception) -> Unit): Result<T> {
  return when (val exception = exceptionOrNull()) {
    null -> this
    is CancellationException -> throw exception
    !is Exception -> throw exception // 非 Exception は回復不能とみなして再スロー
    else -> {
      action(exception)
      this
    }
  }
}
