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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection

@Composable
fun SettingsContent(
  applicationSettings: ApplicationSettings,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
  onSignOut: () -> Unit,
) {
  Surface(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .padding(24.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      AccountSection(
        onClickSignOut = onSignOut,
      )

      DataCollectionSection(
        applicationSettings = applicationSettings,
        onChangeIsFirebaseCrashlyticsEnabled = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              isFirebaseCrashlyticsEnabled = it,
            )
          }
        },
      )
    }
  }
}

@Composable
private fun AccountSection(onClickSignOut: () -> Unit) {
  SettingSection(title = "アカウント") {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.AutoMirrored.Filled.Logout),
      headlineText = "ログアウト",
      modifier = Modifier.clickable(onClick = onClickSignOut),
    )
  }
}

@Composable
private fun DataCollectionSection(
  applicationSettings: ApplicationSettings,
  onChangeIsFirebaseCrashlyticsEnabled: (Boolean) -> Unit,
) {
  SettingSection(title = "データ収集") {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.BugReport),
      headlineText = "バグレポートを自動的に送信する",
      supportingText = "アプリがクラッシュした場合にエラーを Firebase Crashlytics に報告します。送信されるデータは匿名です。",
      trailingContent = {
        Switch(
          checked = applicationSettings.isFirebaseCrashlyticsEnabled,
          onCheckedChange = onChangeIsFirebaseCrashlyticsEnabled,
        )
      },
    )
  }
}

@Preview
@Composable
private fun SettingsContentPreview() {
  SettingsContent(
    applicationSettings = ApplicationSettings.Default,
    onChangeApplicationSettings = { ApplicationSettings.Default },
    onSignOut = {},
  )
}
