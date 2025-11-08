package blue.starry.mitsubachi.ui.feature.photowidget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Receiver for the Photo Widget
 */
class PhotoWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = PhotoWidget()
}
