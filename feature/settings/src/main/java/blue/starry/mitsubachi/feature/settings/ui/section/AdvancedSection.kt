package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.domain.model.UserSettings
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.settings.R

@Composable
@Suppress("LongMethod")
internal fun AdvancedSection(
  userSettings: UserSettings,
  onChangeUserSettings: ((UserSettings) -> UserSettings) -> Unit,
) {
  var activeTextFieldDialog by remember { mutableStateOf<TextFieldType?>(null) }

  SettingSection(title = stringResource(R.string.advanced_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.api),
      headline = {
        Text(stringResource(R.string.swarm_compatibility_mode_headline))
      },
      supporting = {
        Text(stringResource(R.string.swarm_compatibility_mode_supporting))
      },
      trailing = {
        Switch(
          checked = userSettings.useSwarmCompatibilityMode,
          onCheckedChange = {
            onChangeUserSettings { settings ->
              settings.copy(
                useSwarmCompatibilityMode = it,
              )
            }
          },
        )
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.key),
      headline = {
        Text(stringResource(R.string.swarm_oauth_token_headline))
      },
      supporting = {
        SecretStringValue(userSettings.swarmOAuthToken)
      },
      modifier = Modifier.clickable {
        activeTextFieldDialog = TextFieldType.SwarmOAuthToken
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Blank,
      headline = {
        Text(stringResource(R.string.unique_device_headline))
      },
      supporting = {
        StringValue(userSettings.uniqueDevice)
      },
      modifier = Modifier.clickable {
        activeTextFieldDialog = TextFieldType.UniqueDevice
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Blank,
      headline = {
        Text(stringResource(R.string.wsid_headline))
      },
      supporting = {
        StringValue(userSettings.wsid)
      },
      modifier = Modifier.clickable {
        activeTextFieldDialog = TextFieldType.Wsid
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Blank,
      headline = {
        Text(stringResource(R.string.user_agent_headline))
      },
      supporting = {
        StringValue(userSettings.userAgent)
      },
      modifier = Modifier.clickable {
        activeTextFieldDialog = TextFieldType.UserAgent
      },
    )
  }

  activeTextFieldDialog?.also { type ->
    val (initialValue, @StringRes titleResId) = when (type) {
      TextFieldType.SwarmOAuthToken -> userSettings.swarmOAuthToken to R.string.swarm_oauth_token_headline
      TextFieldType.UniqueDevice -> userSettings.uniqueDevice to R.string.unique_device_headline
      TextFieldType.Wsid -> userSettings.wsid to R.string.wsid_headline
      TextFieldType.UserAgent -> userSettings.userAgent to R.string.user_agent_headline
    }

    TextFieldDialog(
      initialValue = initialValue.orEmpty(),
      titleResId = titleResId,
      onDismiss = {
        activeTextFieldDialog = null
      },
      onConfirm = { value ->
        onChangeUserSettings { settings ->
          val newValue = value.ifBlank { null }
          when (type) {
            TextFieldType.SwarmOAuthToken -> settings.copy(
              swarmOAuthToken = newValue,
            )

            TextFieldType.UniqueDevice -> settings.copy(
              uniqueDevice = newValue,
            )

            TextFieldType.Wsid -> settings.copy(
              wsid = newValue,
            )

            TextFieldType.UserAgent -> settings.copy(
              userAgent = newValue,
            )
          }
        }
        activeTextFieldDialog = null
      },
    )
  }
}

@Composable
private fun StringValue(value: String?) {
  if (value.isNullOrBlank()) {
    Text(stringResource(R.string.not_set))
  } else {
    Text(value)
  }
}

@Composable
private fun SecretStringValue(value: String?) {
  if (value.isNullOrBlank()) {
    Text(stringResource(R.string.not_set))
  } else {
    Text("***")
  }
}

private enum class TextFieldType {
  SwarmOAuthToken,
  UniqueDevice,
  Wsid,
  UserAgent,
}

@Composable
private fun TextFieldDialog(
  initialValue: String,
  @StringRes titleResId: Int,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  var value by remember { mutableStateOf(initialValue) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(titleResId),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      TextField(
        value = value,
        onValueChange = {
          value = it
        },
        singleLine = true,
        maxLines = 1,
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(value) }) {
        Text(text = stringResource(R.string.save_button))
      }
    },
  )
}
