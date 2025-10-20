package blue.starry.mitsubachi.ui.feature.welcome

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.component1
import androidx.activity.result.component2
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.usecase.BeginAuthorizationUseCase
import blue.starry.mitsubachi.domain.usecase.FinishAuthorizationUseCase
import blue.starry.mitsubachi.ui.AccountEventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInWithFoursquareButtonViewModel @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val beginAuthorizationUseCase: BeginAuthorizationUseCase,
  private val finishAuthorizationUseCase: FinishAuthorizationUseCase,
) : ViewModel(), AccountEventHandler {
  sealed interface UiState {
    data object Pending : UiState
    data object Authorized : UiState
    data class Failed(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Pending)
  val state = _state.asStateFlow()

  fun createAuthorizationIntent(): Intent {
    return beginAuthorizationUseCase()
  }

  fun onAuthorizationActivityResult(result: ActivityResult) {
    val (resultCode, data) = result

    // なんらかの理由で Activity が異常終了した場合は無視
    if (data == null || resultCode != Activity.RESULT_OK) {
      return
    }

    viewModelScope.launch {
      try {
        finishAuthorizationUseCase(data)

        _state.value = UiState.Authorized
      } catch (e: Exception) {
        _state.value = UiState.Failed(e)
      }
    }
  }

  override fun onAccountDeleted() {
    _state.value = UiState.Pending
  }
}
