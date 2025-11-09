package blue.starry.mitsubachi.feature.photowidget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetStateDefinition
import blue.starry.mitsubachi.feature.photowidget.ui.PhotoWidgetContent

class PhotoWidget : GlanceAppWidget() {
  override val stateDefinition = PhotoWidgetStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      GlanceTheme {
        PhotoWidgetContent()
      }
    }
  }

  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    // TODO: Add preview
    super.providePreview(context, widgetCategory)
  }
}
