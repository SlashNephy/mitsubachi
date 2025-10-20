package blue.starry.mitsubachi.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnackbarViewModel @Inject constructor() : ViewModel() {
  @Immutable
  data class Message(val text: String)

  private val _messages = MutableSharedFlow<Message>()
  val messages = _messages.asSharedFlow()

  fun enqueue(message: Message) {
    viewModelScope.launch {
      _messages.emit(message)
    }
  }

  fun enqueue(text: String) {
    enqueue(Message(text))
  }
}
