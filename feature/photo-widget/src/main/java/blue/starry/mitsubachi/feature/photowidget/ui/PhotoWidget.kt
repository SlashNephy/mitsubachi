package blue.starry.mitsubachi.feature.photowidget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent

/**
 * Glance AppWidget that displays a random photo from user's check-ins
 */
class PhotoWidget : GlanceAppWidget() {
  override val stateDefinition = PhotoWidgetStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      GlanceTheme {
        PhotoWidgetContent(context)
      }
    }
  }

  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    // TODO: Add preview
    super.providePreview(context, widgetCategory)
  }
}
