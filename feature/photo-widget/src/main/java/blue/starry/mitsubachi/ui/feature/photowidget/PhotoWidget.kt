package blue.starry.mitsubachi.ui.feature.photowidget

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.io.File

/**
 * Glance AppWidget that displays a random photo from user's check-ins
 */
class PhotoWidget : GlanceAppWidget() {
  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      GlanceTheme {
        PhotoWidgetContent(context)
      }
    }
  }
}

@Composable
private fun PhotoWidgetContent(context: Context) {
  val prefs = context.getSharedPreferences(
    PhotoWidgetUpdateWorker.WIDGET_PREFS_NAME,
    Context.MODE_PRIVATE,
  )
  val photoAvailable = prefs.getBoolean(PhotoWidgetUpdateWorker.PREF_KEY_PHOTO_AVAILABLE, false)

  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .background(GlanceTheme.colors.surface)
      .cornerRadius(16.dp)
      .padding(4.dp),
    contentAlignment = Alignment.Center,
  ) {
    if (photoAvailable) {
      // Load the image from internal storage
      val file = File(context.filesDir, PhotoWidgetUpdateWorker.WIDGET_PHOTO_FILENAME)
      if (file.exists()) {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if (bitmap != null) {
          Image(
            provider = ImageProvider(bitmap),
            contentDescription = "Check-in photo",
            modifier = GlanceModifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
          )
        } else {
          PlaceholderText()
        }
      } else {
        PlaceholderText()
      }
    } else {
      PlaceholderText()
    }
  }
}

@Composable
private fun PlaceholderText() {
  Text(
    text = "No photos yet",
    style = TextStyle(
      color = GlanceTheme.colors.onSurface,
    ),
  )
}
