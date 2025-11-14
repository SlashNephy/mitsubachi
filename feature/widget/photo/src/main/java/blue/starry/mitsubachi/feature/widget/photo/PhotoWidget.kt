package blue.starry.mitsubachi.feature.widget.photo

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import blue.starry.mitsubachi.core.ui.glance.MitsubachiGlanceTheme
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetStateDefinition
import blue.starry.mitsubachi.feature.widget.photo.ui.PhotoWidgetContent

class PhotoWidget : GlanceAppWidget() {
  override val stateDefinition = PhotoWidgetStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      MitsubachiGlanceTheme {
        PhotoWidgetContent()
      }
    }
  }

  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    // TODO: Add preview
  }
}
