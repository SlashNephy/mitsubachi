package blue.starry.mitsubachi.core.ui.compose.error

inline fun <T> Result<T>.onException(action: (exception: Exception) -> Unit): Result<T> {
  exceptionOrNull()?.also { throwable ->
    if (throwable !is Exception) {
      // 非 Exception は回復不能とみなして再スロー
      throw throwable
    }

    action(throwable)
  }

  return this
}
