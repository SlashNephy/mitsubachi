package blue.starry.mitsubachi.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.usecase.SignOutUseCase
import blue.starry.mitsubachi.ui.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
  private val signOutUseCase: SignOutUseCase,
  private val errorHandler: ErrorHandler,
) : ViewModel() {
  fun signOut(): Job {
    return viewModelScope.launch {
      runCatching {
        signOutUseCase()
      }.onFailure { e ->
        errorHandler.handle(e)
      }
    }
  }
}
