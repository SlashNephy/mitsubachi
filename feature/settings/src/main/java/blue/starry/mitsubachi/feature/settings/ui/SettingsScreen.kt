package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.ui.compose.error.onSuccess
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import kotlin.time.Duration.Companion.minutes

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
        account = state.account,
        applicationSettings = state.applicationSettings,
        userSettings = state.userSettings,
        applicationConfig = viewModel.applicationConfig,
        onChangeApplicationSettings = { block ->
          viewModel.updateApplicationSettings { applicationSettings ->
            applicationSettings.let(block)
          }
        },
        onChangeUserSettings = { block ->
          viewModel.updateUserSettings { userSettings ->
            userSettings.let(block)
          }
        },
        onSignOut = {
          viewModel.signOut().onSuccess { onSignOut() }
        },
        onUpdateWidgetSchedule = viewModel::onUpdatePhotoWidgetSchedule,
        formatDuration = { duration ->
          viewModel.formatDuration(duration, 5.minutes)
        },
      )
    }
  }
}
