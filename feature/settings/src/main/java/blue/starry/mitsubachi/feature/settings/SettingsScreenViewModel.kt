package blue.starry.mitsubachi.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.usecase.AppSettingsRepository
import blue.starry.mitsubachi.domain.usecase.SignOutUseCase
import blue.starry.mitsubachi.ui.error.SnackbarErrorPresenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
  private val signOutUseCase: SignOutUseCase,
  private val appSettingsRepository: AppSettingsRepository,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
) : ViewModel() {
  val isFirebaseCrashlyticsEnabled = appSettingsRepository.isFirebaseCrashlyticsEnabled.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = true,
  )

  fun signOut(): Job {
    return viewModelScope.launch {
      runCatching {
        signOutUseCase()
      }.onFailure { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }

  fun setFirebaseCrashlyticsEnabled(isEnabled: Boolean) {
    viewModelScope.launch {
      runCatching {
        appSettingsRepository.setFirebaseCrashlyticsEnabled(isEnabled)
      }.onFailure { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }
}
