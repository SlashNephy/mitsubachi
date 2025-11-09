package blue.starry.mitsubachi.feature.photowidget.ui

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages scheduling of photo widget updates
 */
@Singleton
class PhotoWidgetWorkScheduler @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  /**
   * Schedules periodic updates for the photo widget
   */
  fun schedulePeriodicUpdate() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val workRequest = PeriodicWorkRequestBuilder<PhotoWidgetUpdateWorker>(
      repeatInterval = 6,
      repeatIntervalTimeUnit = TimeUnit.HOURS,
    )
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      PhotoWidgetUpdateWorker.WORK_NAME,
      ExistingPeriodicWorkPolicy.KEEP,
      workRequest,
    )
  }

  /**
   * Cancels scheduled updates
   */
  fun cancelPeriodicUpdate() {
    WorkManager.getInstance(context).cancelUniqueWork(PhotoWidgetUpdateWorker.WORK_NAME)
  }
}
