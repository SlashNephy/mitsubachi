package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal class SettingSectionScopeImpl : SettingSectionScope {
  val items = mutableListOf<SettingItem>()

  override fun item(item: SettingItem) {
    items.add(item)
  }

  override fun item(
    headline: @Composable () -> Unit,
    modifier: Modifier,
    overline: @Composable (() -> Unit)?,
    supporting: @Composable (() -> Unit)?,
    leadingIcon: SettingItem.LeadingIcon,
    trailing: @Composable (() -> Unit)?,
  ) {
    item(
      SettingItem(
        headline = headline,
        modifier = modifier,
        overline = overline,
        supporting = supporting,
        leadingIcon = leadingIcon,
        trailing = trailing,
      ),
    )
  }
}
