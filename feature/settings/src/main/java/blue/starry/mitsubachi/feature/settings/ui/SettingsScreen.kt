package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen

@Composable
fun SettingsScreen(
  onSignOut: () -> Unit,
  viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  when (val state = state) {
    is SettingsScreenViewModel.UiState.Loading -> {
      LoadingScreen()
    }

    is SettingsScreenViewModel.UiState.Loaded -> {
      SettingsContent(
        applicationSettings = state.applicationSettings,
        onChangeApplicationSettings = { block ->
          viewModel.update { settings ->
            settings.let(block)
          }
        },
        onSignOut = {
          viewModel.signOut().invokeOnCompletion { onSignOut() }
        },
        formatDuration = viewModel::formatDuration,
      )
    }
  }
}
