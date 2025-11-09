package blue.starry.mitsubachi.ui.feature.photowidget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Receiver for the Photo Widget
 */
@AndroidEntryPoint
class PhotoWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = PhotoWidget()

  @Suppress("LateinitUsage")
  @Inject
  lateinit var photoWidgetWorkScheduler: PhotoWidgetWorkScheduler

  override fun onEnabled(context: Context) {
    super.onEnabled(context)
    // Schedule worker when first widget is added
    photoWidgetWorkScheduler.schedulePeriodicUpdate()
  }

  override fun onDisabled(context: Context) {
    super.onDisabled(context)
    // Cancel worker when last widget is removed
    photoWidgetWorkScheduler.cancelPeriodicUpdate()
  }
}
