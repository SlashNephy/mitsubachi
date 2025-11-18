package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.feature.settings.R

@Composable
fun SettingsContent(
  applicationSettings: ApplicationSettings,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
  onSignOut: () -> Unit,
) {
  Surface(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier.padding(24.dp),
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
  val context = LocalContext.current
  val showLogoutDialog = remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.account_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.AutoMirrored.Filled.Logout),
      headlineText = context.getString(R.string.logout_button),
      modifier = Modifier.clickable(onClick = { showLogoutDialog.value = true }),
    )
  }

  if (showLogoutDialog.value) {
    LogoutConfirmationDialog(
      onConfirm = {
        showLogoutDialog.value = false
        onClickSignOut()
      },
      onDismiss = {
        showLogoutDialog.value = false
      },
    )
  }
}

@Composable
private fun LogoutConfirmationDialog(
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(R.string.logout_button),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      Text(
        text = stringResource(R.string.logout_confirm_body),
        style = MaterialTheme.typography.bodyMedium,
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(onClick = onConfirm) {
        Text(text = stringResource(R.string.logout_button))
      }
    },
  )
}

@Composable
private fun DataCollectionSection(
  applicationSettings: ApplicationSettings,
  onChangeIsFirebaseCrashlyticsEnabled: (Boolean) -> Unit,
) {
  val context = LocalContext.current

  SettingSection(title = stringResource(R.string.data_collection_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.BugReport),
      headlineText = context.getString(R.string.optin_firebase_crashlytics_headline),
      supportingText = context.getString(R.string.optin_firebase_crashlytics_supporting),
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
