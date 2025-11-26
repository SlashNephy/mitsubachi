package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

data class SettingItem(
  val headline: @Composable () -> Unit,
  val modifier: Modifier,
  val overline: @Composable (() -> Unit)?,
  val supporting: @Composable (() -> Unit)?,
  val leadingIcon: LeadingIcon,
  val trailing: @Composable (() -> Unit)?,
) {
  sealed interface LeadingIcon {
    data class Flat(val painter: Painter) : LeadingIcon
    data class Round(val painter: Painter) : LeadingIcon
    data object Blank : LeadingIcon
  }
}
