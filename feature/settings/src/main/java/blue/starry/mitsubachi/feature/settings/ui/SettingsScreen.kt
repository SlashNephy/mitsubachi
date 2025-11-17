package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
      Surface(modifier = Modifier.fillMaxSize()) {
        Column(
          modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          AccountSection(
            onClickLogout = {
              viewModel.signOut().invokeOnCompletion { onSignOut() }
            },
          )

          DataCollectionSection(
            settings = state.applicationSettings,
            onChangeIsFirebaseCrashlyticsEnabled = {
              viewModel.update { settings ->
                settings.copy(
                  isFirebaseCrashlyticsEnabled = it,
                )
              }
            },
          )
        }
      }
    }
  }
}

@Composable
private fun AccountSection(onClickLogout: () -> Unit) {
  SettingSection(title = "アカウント") {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.AutoMirrored.Filled.Logout),
      headlineText = "ログアウト",
      modifier = Modifier.clickable(onClick = onClickLogout),
    )
  }
}

@Composable
private fun DataCollectionSection(
  settings: ApplicationSettings,
  onChangeIsFirebaseCrashlyticsEnabled: (Boolean) -> Unit,
) {
  SettingSection(title = "データ収集") {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.BugReport),
      headlineText = "バグレポートを自動的に送信する",
      supportingText = "アプリがクラッシュした場合にエラーを Firebase Crashlytics に報告します。送信されるデータは匿名です。",
      trailingContent = {
        Switch(
          checked = settings.isFirebaseCrashlyticsEnabled,
          onCheckedChange = onChangeIsFirebaseCrashlyticsEnabled,
        )
      },
    )
  }
}

@Preview
@Composable
private fun SettingsScreenLoadingPreview() {
  SettingsScreen(
    onSignOut = {},
    viewModel = mockk {
      every { state } returns MutableStateFlow(SettingsScreenViewModel.UiState.Loading).asStateFlow()
    },
  )
}

@Preview
@Composable
private fun SettingsScreenLoadedPreview() {
  SettingsScreen(
    onSignOut = {},
    viewModel = mockk {
      every { state } returns MutableStateFlow(
        SettingsScreenViewModel.UiState.Loaded(
          applicationSettings = mockk(relaxed = true),
        ),
      ).asStateFlow()
    },
  )
}
