package blue.starry.mitsubachi.core.ui.compose.snackbar

import kotlinx.coroutines.flow.SharedFlow

interface SnackbarHostService {
  val messages: SharedFlow<SnackbarMessage>
  suspend fun enqueue(message: SnackbarMessage)
}

suspend fun SnackbarHostService.enqueue(message: String) {
  enqueue(SnackbarMessage(message))
}
