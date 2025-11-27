package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class SettingItem(
  val headline: @Composable () -> Unit,
  val modifier: Modifier,
  val overline: @Composable (() -> Unit)?,
  val supporting: @Composable (() -> Unit)?,
  val leadingIcon: LeadingIcon,
  val trailing: @Composable (() -> Unit)?,
) {
  sealed interface LeadingIcon {
    data class Flat(@param:DrawableRes val id: Int) : LeadingIcon
    data class Round(@param:DrawableRes val id: Int) : LeadingIcon
    data object Blank : LeadingIcon
  }
}
