package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal class SettingSectionScopeImpl : SettingSectionScope {
  val items = mutableListOf<SettingItem>()

  override fun item(item: SettingItem) {
    items.add(item)
  }

  override fun item(
    headlineText: String,
    modifier: Modifier,
    overlineText: String?,
    supportingText: String?,
    leadingIcon: SettingItem.LeadingIcon,
    trailingContent: @Composable (() -> Unit)?,
  ) {
    item(
      SettingItem(
        headlineText = headlineText,
        modifier = modifier,
        overlineText = overlineText,
        supportingText = supportingText,
        leadingIcon = leadingIcon,
        trailingContent = trailingContent,
      ),
    )
  }
}
