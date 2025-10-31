package blue.starry.mitsubachi.ui.feature.welcome

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SignInWithFoursquareButton(
  onSuccess: () -> Unit,
  viewModel: SignInWithFoursquareButtonViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  LaunchedEffect(state) {
    when (val state = state) {
      is SignInWithFoursquareButtonViewModel.UiState.Authorized -> {
        onSuccess()
      }

      else -> {}
    }
  }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    onResult = viewModel::onAuthorizationActivityResult,
  )

  Button(
    onClick = {
      val intent = viewModel.createAuthorizationIntent()
      launcher.launch(intent)
    },
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(stringResource(R.string.sign_in_button))
  }
}
