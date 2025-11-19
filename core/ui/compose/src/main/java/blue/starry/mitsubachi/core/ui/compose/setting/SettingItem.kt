package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingItem(
  val headline: @Composable () -> Unit,
  val modifier: Modifier,
  val overline: @Composable (() -> Unit)?,
  val supporting: @Composable (() -> Unit)?,
  val leadingIcon: LeadingIcon,
  val trailing: @Composable (() -> Unit)?,
) {
  sealed interface LeadingIcon {
    data class Flat(val imageVector: ImageVector) : LeadingIcon
    data class Round(val imageVector: ImageVector) : LeadingIcon
    data object Blank : LeadingIcon
  }
}
