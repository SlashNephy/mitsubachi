package blue.starry.mitsubachi.feature.photowidget.ui

import android.content.Context
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
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.io.File
import java.time.format.DateTimeFormatter

@Composable
internal fun PhotoWidgetContent(context: Context) {
  val state = currentState<PhotoWidgetState>()

  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .background(GlanceTheme.colors.surface)
      .cornerRadius(16.dp)
      .padding(4.dp),
    contentAlignment = Alignment.Center,
  ) {
    when {
      !state.isLoggedIn -> NotLoggedInContent()
      !state.hasPhoto || state.checkInId == null -> NoPhotosContent()
      else -> PhotoContent(context, state)
    }
  }
}

@Composable
private fun NotLoggedInContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = GlanceModifier.padding(16.dp),
  ) {
    Text(
      text = "Not logged in",
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )
    Spacer(modifier = GlanceModifier.height(8.dp))
    Text(
      text = "Please log in to see your check-in photos",
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}

@Composable
private fun NoPhotosContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = GlanceModifier.padding(16.dp),
  ) {
    Text(
      text = "No photos yet",
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )
    Spacer(modifier = GlanceModifier.height(8.dp))
    Text(
      text = "Add photos to your check-ins to see them here",
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}

@Composable
private fun PhotoContent(context: Context, state: PhotoWidgetState) {
  val file = File(context.filesDir, PhotoWidgetUpdateWorker.WIDGET_PHOTO_FILENAME)
  val bitmap = loadBitmap(file)

  if (bitmap != null) {
    PhotoWithOverlay(state, bitmap)
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
private fun PhotoWithOverlay(state: PhotoWidgetState, bitmap: Bitmap) {
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
private fun PhotoOverlay(state: PhotoWidgetState) {
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
      state.venueName?.let { venueName ->
        Text(
          text = venueName,
          style = TextStyle(
            color = GlanceTheme.colors.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
      }
      state.checkInDate?.let { date ->
        Text(
          text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
          style = TextStyle(
            color = GlanceTheme.colors.onSurface,
            fontSize = 12.sp,
          ),
        )
      }
    }
  }
}
