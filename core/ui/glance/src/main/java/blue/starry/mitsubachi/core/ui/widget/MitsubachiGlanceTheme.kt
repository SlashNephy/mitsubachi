package blue.starry.mitsubachi.core.ui.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme

@Composable
fun MitsubachiGlanceTheme(
  dynamicColor: Boolean = true,
  content:
  @Composable @GlanceComposable () -> Unit,
) {
  val colors = if (dynamicColor) {
    GlanceTheme.colors
  } else {
    MitsubachiGlanceColorScheme.colors
  }

  GlanceTheme(
    colors = colors,
    content = content,
  )
}
