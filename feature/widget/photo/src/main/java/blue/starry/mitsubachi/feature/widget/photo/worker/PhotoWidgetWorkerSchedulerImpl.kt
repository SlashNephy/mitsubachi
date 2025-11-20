package blue.starry.mitsubachi.feature.widget.photo.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import blue.starry.mitsubachi.core.domain.usecase.DeviceNetworkRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Singleton
internal class PhotoWidgetWorkerSchedulerImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val applicationSettingsRepository: ApplicationSettingsRepository,
  private val deviceNetworkRepository: DeviceNetworkRepository,
) : PhotoWidgetWorkerScheduler {
  companion object {
    private const val WORKER_NAME = "photo_widget_worker"
  }

  override suspend fun enqueue() {
    val networkType = if (preferUnmeteredNetwork()) {
      NetworkType.UNMETERED
    } else {
      NetworkType.CONNECTED
    }

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(networkType)
      .setRequiresBatteryNotLow(true)
      .build()

    val interval = maxOf(
      applicationSettingsRepository.select { it.widgetUpdateInterval },
      15.minutes,
    ).toJavaDuration()
    val request = PeriodicWorkRequestBuilder<PhotoWidgetWorker>(interval)
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      WORKER_NAME,
      ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
      request,
    )
  }

  private suspend fun preferUnmeteredNetwork(): Boolean {
    // 次の場合は無制限のネットワークを優先
    // - isWidgetUpdateOnUnmeteredNetworkOnlyEnabled が有効
    // - データセーバーモードが有効
    return applicationSettingsRepository.select { it.isWidgetUpdateOnUnmeteredNetworkOnlyEnabled } ||
            deviceNetworkRepository.isDataSaverEnabled()
  }

  override suspend fun cancel() {
    WorkManager.getInstance(context).cancelUniqueWork(WORKER_NAME)
  }
}
