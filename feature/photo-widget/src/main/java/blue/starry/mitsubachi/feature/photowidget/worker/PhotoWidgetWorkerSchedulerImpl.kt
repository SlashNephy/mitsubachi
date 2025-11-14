package blue.starry.mitsubachi.feature.photowidget.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import blue.starry.mitsubachi.core.domain.usecase.DeviceNetworkRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Singleton
internal class PhotoWidgetWorkerSchedulerImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val deviceNetworkRepository: DeviceNetworkRepository,
) : PhotoWidgetWorkerScheduler {
  companion object {
    private const val WORKER_NAME = "photo_widget_worker"
    private val interval = 3.hours.toJavaDuration()
  }

  override fun enqueue() {
    // データセーバーモードが有効な場合は無制限のネットワークでのみ実行
    val networkType = if (deviceNetworkRepository.isDataSaverEnabled()) {
      NetworkType.UNMETERED
    } else {
      NetworkType.CONNECTED
    }

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(networkType)
      .setRequiresBatteryNotLow(true)
      .build()

    val request = PeriodicWorkRequestBuilder<PhotoWidgetWorker>(interval)
      .setConstraints(constraints)
      .build()

    WorkManager.Companion.getInstance(context).enqueueUniquePeriodicWork(
      WORKER_NAME,
      ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
      request,
    )
  }

  override fun cancel() {
    WorkManager.Companion.getInstance(context).cancelUniqueWork(WORKER_NAME)
  }
}
