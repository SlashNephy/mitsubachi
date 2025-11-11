package blue.starry.mitsubachi.feature.photowidget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetState
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun PhotoOverlay(photo: PhotoWidgetState.Photo) {
  val context = LocalContext.current
  val surfaceColor = GlanceTheme.colors.surface.getColor(context).copy(alpha = 0.7f)

  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(8.dp),
    contentAlignment = Alignment.BottomStart,
  ) {
    Column(
      modifier = GlanceModifier
        .background(surfaceColor)
        .cornerRadius(8.dp)
        .padding(8.dp),
    ) {
      Text(
        text = photo.venueName,
        style = TextStyle(
          color = GlanceTheme.colors.onSurface,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
        ),
      )

      if (photo.venueAddress.isNotEmpty()) {
        Text(
          text = photo.venueAddress,
          style = TextStyle(
            color = GlanceTheme.colors.onSurface,
            fontSize = 11.sp,
          ),
        )
      }

      val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
      Text(
        text = photo.checkInAt.format(formatter),
        style = TextStyle(
          color = GlanceTheme.colors.onSurface,
          fontSize = 12.sp,
        ),
      )
    }
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun PhotoOverlayPreview() {
  PhotoOverlay(
    photo = PhotoWidgetState.Photo(
      id = "photo",
      path = "photo.jpg",
      checkInId = "check_in",
      venueName = "ぷれびゅーパーク",
      venueAddress = "東京都渋谷区",
      checkInAt = ZonedDateTime.now().minusDays(7),
    ),
  )
}
