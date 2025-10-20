package blue.starry.mitsubachi.ui.feature.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.ui.SnackbarViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun WelcomeScreen(
  onLogin: () -> Unit,
  viewModel: WelcomeScreenViewModel = hiltViewModel(),
  snackbarViewModel: SnackbarViewModel = hiltViewModel(),
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize(),
  ) {
    val scope = rememberCoroutineScope()
    val resources = LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val state = state) {
      is WelcomeScreenViewModel.UiState.Loading -> {
        LoadingIndicator()
      }

      is WelcomeScreenViewModel.UiState.NoAccounts -> {
        SignInWithFoursquareButton(onSuccess = {
          onLogin()
          scope.launch {
            snackbarViewModel.enqueue(resources.getString(R.string.login_succeeded))
          }
        }, onFailure = {
          scope.launch {
            snackbarViewModel.enqueue(resources.getString(R.string.login_failed))
          }
        })
      }

      is WelcomeScreenViewModel.UiState.HasAccount -> {
        LaunchedEffect(state) {
          onLogin()
        }
      }
    }
  }
}
