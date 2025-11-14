package blue.starry.mitsubachi.core.ui.compose.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarHostServiceImpl @Inject constructor() : SnackbarHostService {
  private val _messages = MutableSharedFlow<SnackbarMessage>()
  override val messages: SharedFlow<SnackbarMessage> = _messages.asSharedFlow()

  override suspend fun enqueue(message: SnackbarMessage) {
    _messages.emit(message)
  }
}
