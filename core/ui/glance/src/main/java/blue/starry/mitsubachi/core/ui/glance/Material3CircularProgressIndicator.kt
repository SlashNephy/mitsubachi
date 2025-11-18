package blue.starry.mitsubachi.core.ui.glance

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.unit.ColorProvider

@Composable
fun Material3CircularProgressIndicator(
  modifier: GlanceModifier = GlanceModifier,
  color: ColorProvider = GlanceTheme.colors.primary,
) {
  CircularProgressIndicator(
    color = color,
    modifier = modifier,
  )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Composable
private fun Material3CircularProgressIndicatorPreview() {
  Material3CircularProgressIndicator()
}
