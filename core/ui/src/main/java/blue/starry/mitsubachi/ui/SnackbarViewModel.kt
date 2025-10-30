package blue.starry.mitsubachi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.ui.snackbar.SnackbarHostService
import blue.starry.mitsubachi.ui.snackbar.SnackbarMessage
import blue.starry.mitsubachi.ui.snackbar.enqueue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: この実装を削除。Snackbar へのアクセスは SnackbarHostService を経由させる。
@HiltViewModel
@Deprecated("Use SnackbarHostService instead.")
class SnackbarViewModel @Inject constructor(
  private val service: SnackbarHostService,
) : ViewModel() {
  val messages: SharedFlow<SnackbarMessage>
    get() = service.messages

  fun enqueue(text: String) {
    viewModelScope.launch {
      service.enqueue(text)
    }
  }
}
