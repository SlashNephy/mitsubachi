package blue.starry.mitsubachi.feature.photowidget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import blue.starry.mitsubachi.feature.photowidget.worker.PhotoWidgetWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhotoWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = PhotoWidget()

  @Suppress("LateinitUsage")
  @Inject
  lateinit var workerScheduler: PhotoWidgetWorkerScheduler

  override fun onEnabled(context: Context) {
    super.onEnabled(context)
    workerScheduler.enqueue()
  }

  override fun onDisabled(context: Context) {
    super.onDisabled(context)
    workerScheduler.cancel()
  }
}
