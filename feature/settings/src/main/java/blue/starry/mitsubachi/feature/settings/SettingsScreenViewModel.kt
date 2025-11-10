package blue.starry.mitsubachi.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.usecase.SettingsRepository
import blue.starry.mitsubachi.domain.usecase.SignOutUseCase
import blue.starry.mitsubachi.ui.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.ui.error.onException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
  private val signOutUseCase: SignOutUseCase,
  private val settingsRepository: SettingsRepository,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
) : ViewModel() {
  val isFirebaseCrashlyticsEnabled = settingsRepository.isFirebaseCrashlyticsEnabled.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = true,
  )

  fun signOut(): Job {
    return viewModelScope.launch {
      runCatching {
        signOutUseCase()
      }.onException { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }

  fun setFirebaseCrashlyticsEnabled(isEnabled: Boolean): Job {
    return viewModelScope.launch {
      runCatching {
        settingsRepository.setFirebaseCrashlyticsEnabled(isEnabled)
      }.onException { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }
}
