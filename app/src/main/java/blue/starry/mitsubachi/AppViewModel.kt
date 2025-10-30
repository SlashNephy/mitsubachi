package blue.starry.mitsubachi

import androidx.lifecycle.ViewModel
import blue.starry.mitsubachi.ui.snackbar.SnackbarHostService
import blue.starry.mitsubachi.ui.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
  private val snackbarHostService: SnackbarHostService,
) : ViewModel() {
  val snackbarMessages: SharedFlow<SnackbarMessage>
    get() = snackbarHostService.messages
}
