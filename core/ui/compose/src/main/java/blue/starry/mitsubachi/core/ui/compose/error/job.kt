package blue.starry.mitsubachi.core.ui.compose.error

import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job

fun Job.onSuccess(action: () -> Unit): DisposableHandle {
  return invokeOnCompletion { cause ->
    if (cause == null) {
      action()
    }
  }
}
