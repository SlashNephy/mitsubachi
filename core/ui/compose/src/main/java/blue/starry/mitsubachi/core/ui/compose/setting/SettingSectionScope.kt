package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface SettingSectionScope {
  fun item(item: SettingItem)

  fun item(
    headline: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overline: @Composable (() -> Unit)? = null,
    supporting: @Composable (() -> Unit)? = null,
    leadingIcon: SettingItem.LeadingIcon = SettingItem.LeadingIcon.Blank,
    trailing: @Composable (() -> Unit)? = null,
  )
}
