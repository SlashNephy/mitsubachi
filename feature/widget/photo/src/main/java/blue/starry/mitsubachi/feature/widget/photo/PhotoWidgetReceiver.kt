package blue.starry.mitsubachi.feature.widget.photo

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import blue.starry.mitsubachi.core.domain.ApplicationScope
import blue.starry.mitsubachi.core.domain.usecase.PhotoWidgetWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhotoWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = PhotoWidget()

  @Suppress("LateinitUsage")
  @Inject
  @ApplicationScope
  lateinit var applicationScope: CoroutineScope

  @Suppress("LateinitUsage")
  @Inject
  lateinit var workerScheduler: PhotoWidgetWorkerScheduler

  override fun onEnabled(context: Context) {
    super.onEnabled(context)
    applicationScope.launch {
      workerScheduler.enqueue()
    }
  }

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray,
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    applicationScope.launch {
      workerScheduler.enqueue()
    }
  }

  override fun onDisabled(context: Context) {
    super.onDisabled(context)
    applicationScope.launch {
      workerScheduler.cancel()
    }
  }
}
