package blue.starry.mitsubachi.feature.welcome.ui

import android.content.Context
import android.content.Intent
import androidx.browser.auth.AuthTabIntent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.OAuth2AuthorizationRequest
import blue.starry.mitsubachi.core.domain.model.OAuth2AuthorizationResponse
import blue.starry.mitsubachi.core.domain.usecase.BeginAuthorizationUseCase
import blue.starry.mitsubachi.core.domain.usecase.FinishAuthorizationUseCase
import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.enqueue
import blue.starry.mitsubachi.feature.welcome.R
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
  private val snackbarHostService: SnackbarHostService,
) : ViewModel() {
  sealed interface UiState {
    data object Pending : UiState
    data class Authorizing(val request: OAuth2AuthorizationRequest) : UiState
    data class Authorized(val account: FoursquareAccount) : UiState
    data object Failed : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Pending)
  val state = _state.asStateFlow()

  fun createAuthorizationIntent(): Intent {
    val request = beginAuthorizationUseCase()
    _state.value = UiState.Authorizing(request)

    val authorizeUri = request.authorizeUrl.toUri()
    return AuthTabIntent.Builder().build().apply {
      intent.setData(authorizeUri)
      intent.putExtra(AuthTabIntent.EXTRA_REDIRECT_SCHEME, request.redirectScheme)
    }.intent
  }

  fun onAuthTabIntentResult(result: AuthTabIntent.AuthResult) {
    // なんらかの理由で Activity が異常終了した場合は無視
    if (result.resultCode != AuthTabIntent.RESULT_OK) {
      return
    }

    val uri = result.resultUri ?: return
    val (request) = state.value as? UiState.Authorizing ?: return
    val response = OAuth2AuthorizationResponse(
      code = uri.getQueryParameter("code") ?: return,
      state = uri.getQueryParameter("state") ?: return,
      request = request,
    )

    viewModelScope.launch {
      runCatching {
        finishAuthorizationUseCase(response)
      }.onSuccess {
        _state.value = UiState.Authorized(it)
        snackbarHostService.enqueue(context.getString(R.string.login_succeeded))
      }.onFailure {
        _state.value = UiState.Failed
        snackbarHostService.enqueue(context.getString(R.string.login_failed))
      }
    }
  }
}
