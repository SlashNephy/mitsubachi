package blue.starry.mitsubachi

import androidx.lifecycle.ViewModel
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
  private val snackbarHostService: SnackbarHostService,
  private val applicationConfig: ApplicationConfig,
) : ViewModel() {
  val snackbarMessages: SharedFlow<SnackbarMessage>
    get() = snackbarHostService.messages

  val isDebugBuild: Boolean
    get() = applicationConfig.isDebugBuild
}
