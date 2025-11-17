package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingItem(
  val headlineText: String,
  val modifier: Modifier,
  val overlineText: String?,
  val supportingText: String?,
  val leadingIcon: LeadingIcon,
  val trailingContent: @Composable (() -> Unit)?,
) {
  sealed interface LeadingIcon {
    data class Flat(val imageVector: ImageVector) : LeadingIcon
    data class Round(val imageVector: ImageVector) : LeadingIcon
    data object Blank : LeadingIcon
  }
}
