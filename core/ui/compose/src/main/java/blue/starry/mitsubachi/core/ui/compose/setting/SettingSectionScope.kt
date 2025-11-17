package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface SettingSectionScope {
  fun item(item: SettingItem)

  fun item(
    headlineText: String,
    modifier: Modifier = Modifier,
    overlineText: String? = null,
    supportingText: String? = null,
    leadingIcon: SettingItem.LeadingIcon = SettingItem.LeadingIcon.Blank,
    trailingContent: @Composable (() -> Unit)? = null,
  )
}
