package blue.starry.mitsubachi.feature.photowidget.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetState
import java.io.File
import java.time.format.DateTimeFormatter

@Composable
internal fun PhotoWidgetContent() {
  val state = currentState<PhotoWidgetState>()

  Scaffold(
    modifier = GlanceModifier
      .fillMaxSize()
      .background(GlanceTheme.colors.surface)
      .cornerRadius(16.dp)
      .padding(4.dp),
  ) {
    when (state) {
      is PhotoWidgetState.Loading -> CircularProgressIndicator()
      is PhotoWidgetState.Photo -> PhotoContent(state)
      is PhotoWidgetState.NoPhotos -> NoPhotosContent()
      is PhotoWidgetState.LoginRequired -> LoginRequiredContent()
    }
  }
}

@Composable
private fun PhotoContent(photo: PhotoWidgetState.Photo) {
  val context = LocalContext.current
  val file = File(photo.path)
  val bitmap = loadBitmap(file)

  if (bitmap != null) {
    PhotoWithOverlay(photo, bitmap)
  } else {
    NoPhotosContent()
  }
}

private fun loadBitmap(file: File): Bitmap? {
  return if (file.exists()) {
    BitmapFactory.decodeFile(file.absolutePath)
  } else {
    null
  }
}

@Composable
private fun PhotoWithOverlay(state: PhotoWidgetState.Photo, bitmap: Bitmap) {
  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .clickable(
        actionStartActivity(
          Intent(
            Intent.ACTION_VIEW,
            "blue.starry.mitsubachi://checkin/${state.checkInId}".toUri(),
          ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
          },
        ),
      ),
  ) {
    Image(
      provider = ImageProvider(bitmap),
      contentDescription = "Check-in photo at ${state.venueName}",
      modifier = GlanceModifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
    )

    PhotoOverlay(state)
  }
}

@Composable
private fun PhotoOverlay(photo: PhotoWidgetState.Photo) {
  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(8.dp),
    contentAlignment = Alignment.BottomStart,
  ) {
    Column(
      modifier = GlanceModifier
        .background(GlanceTheme.colors.surface)
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
      Text(
        text = photo.checkInAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
        style = TextStyle(
          color = GlanceTheme.colors.onSurface,
          fontSize = 12.sp,
        ),
      )
    }
  }
}
