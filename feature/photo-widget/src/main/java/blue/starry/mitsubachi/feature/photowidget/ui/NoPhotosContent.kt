package blue.starry.mitsubachi.feature.photowidget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import blue.starry.mitsubachi.feature.photowidget.R

@Composable
internal fun NoPhotosContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalAlignment = Alignment.CenterVertically,
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    Text(
      text = stringResource(R.string.no_photos_title),
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )

    Spacer(modifier = GlanceModifier.height(16.dp))

    Text(
      text = stringResource(R.string.no_photos_description),
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun NoPhotosContentPreview() {
  NoPhotosContent()
}
