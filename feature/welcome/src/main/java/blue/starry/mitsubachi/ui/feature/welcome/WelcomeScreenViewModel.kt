package blue.starry.mitsubachi.ui.feature.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.FoursquareAccount
import blue.starry.mitsubachi.domain.usecase.FindFoursquareAccountUseCase
import blue.starry.mitsubachi.ui.AccountEventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
) : ViewModel(), AccountEventHandler {
  sealed interface UiState {
    data object Loading : UiState
    data object NoAccounts : UiState
    data class HasAccount(val account: FoursquareAccount) : UiState
  }

  sealed interface OnboardingStep {
    data object Welcome : OnboardingStep
    data object Permissions : OnboardingStep
    data object Login : OnboardingStep
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

  private val _currentStep = MutableStateFlow<OnboardingStep>(OnboardingStep.Welcome)
  val currentStep = _currentStep.asStateFlow()

  private val _permissionGranted = MutableStateFlow(false)
  val permissionGranted = _permissionGranted.asStateFlow()

  init {
    refresh()
  }

  private fun refresh(): Job {
    return viewModelScope.launch {
      fetch()
    }
  }

  private suspend fun fetch() {
    _state.value = UiState.Loading

    val account = findFoursquareAccountUseCase()
    if (account != null) {
      _state.value = UiState.HasAccount(account)
    } else {
      _state.value = UiState.NoAccounts
    }
  }

  fun nextStep() {
    _currentStep.value = when (_currentStep.value) {
      is OnboardingStep.Welcome -> OnboardingStep.Permissions
      is OnboardingStep.Permissions -> OnboardingStep.Login
      is OnboardingStep.Login -> OnboardingStep.Login
    }
  }

  fun previousStep() {
    _currentStep.value = when (_currentStep.value) {
      is OnboardingStep.Welcome -> OnboardingStep.Welcome
      is OnboardingStep.Permissions -> OnboardingStep.Welcome
      is OnboardingStep.Login -> OnboardingStep.Permissions
    }
  }

  fun skipToLogin() {
    _currentStep.value = OnboardingStep.Login
  }

  fun onPermissionGranted(granted: Boolean) {
    _permissionGranted.value = granted
    if (granted) {
      nextStep()
    }
  }

  override fun onAccountDeleted() {
    _state.value = UiState.Loading
  }
}
