package blue.starry.mitsubachi.feature.photowidget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@Composable
internal fun LoadingContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalAlignment = Alignment.CenterVertically,
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(32.dp),
  ) {
    CircularProgressIndicator()
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun LoadingContentPreview() {
  LoadingContent()
}
