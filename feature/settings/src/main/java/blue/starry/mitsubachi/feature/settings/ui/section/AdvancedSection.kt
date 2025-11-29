package blue.starry.mitsubachi.feature.settings.ui.section

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

  SettingSection(title = "高度な設定") {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.api),
      headline = {
        Text("Swarm 互換モード")
      },
      supporting = {
        Text("有効にすると Swarm アプリの挙動が再現されます。少なくとも「Swarm アプリの OAuth トークン」を設定する必要があります。")
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
        Text("Swarm アプリの OAuth トークン")
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
        Text("uniqueDevice")
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
        Text("wsid")
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
        Text("User-Agent")
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
    val (initialValue, title) = when (type) {
      TextFieldType.SwarmOAuthToken -> userSettings.swarmOAuthToken to "Swarm アプリの OAuth トークン"
      TextFieldType.UniqueDevice -> userSettings.uniqueDevice to "uniqueDevice"
      TextFieldType.Wsid -> userSettings.wsid to "wsid"
      TextFieldType.UserAgent -> userSettings.userAgent to "User-Agent"
    }

    TextFieldDialog(
      initialValue = initialValue.orEmpty(),
      title = title,
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
    Text("(未設定)")
  } else {
    Text(value)
  }
}

@Composable
private fun SecretStringValue(value: String?) {
  if (value.isNullOrBlank()) {
    Text("(未設定)")
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
  title: String,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  var value by remember { mutableStateOf(initialValue) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = title,
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
