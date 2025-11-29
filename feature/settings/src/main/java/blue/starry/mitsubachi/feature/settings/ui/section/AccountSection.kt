package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.settings.R

@Composable
internal fun AccountSection(
  account: FoursquareAccount,
  onClickSignOut: () -> Unit,
) {
  var showLogoutDialog by remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.account_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Remote(
        url = account.iconUrl,
        modifier = Modifier.clip(CircleShape),
      ),
      headline = {
        Text(account.displayName)
      },
      supporting = {
        Text(account.email)
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.logout),
      headline = {
        Text(stringResource(R.string.logout_button))
      },
      modifier = Modifier.clickable(onClick = { showLogoutDialog = true }),
    )
  }

  if (showLogoutDialog) {
    LogoutConfirmationDialog(
      onConfirm = {
        showLogoutDialog = false
        onClickSignOut()
      },
      onDismiss = {
        showLogoutDialog = false
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
