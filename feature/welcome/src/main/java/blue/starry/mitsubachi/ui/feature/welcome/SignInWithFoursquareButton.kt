package blue.starry.mitsubachi.ui.feature.welcome

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.browser.auth.AuthTabIntent
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun SignInWithFoursquareButton(
  onSuccess: () -> Unit,
  viewModel: SignInWithFoursquareButtonViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val launcher = rememberLauncherForActivityResult(
    contract = AuthTabIntent.AuthenticateUserResultContract(),
    onResult = viewModel::onAuthTabIntentResult,
  )

  when (state) {
    is SignInWithFoursquareButtonViewModel.UiState.Authorizing -> {
      LoadingIndicator()
    }

    is SignInWithFoursquareButtonViewModel.UiState.Authorized -> {
      LaunchedEffect(state) {
        onSuccess()
      }
    }

    else -> {
      Button(
        onClick = {
          val intent = viewModel.createAuthorizationIntent()
          launcher.launch(intent)
        },
      ) {
        Text(stringResource(R.string.sign_in_button))
      }
    }
  }
}
