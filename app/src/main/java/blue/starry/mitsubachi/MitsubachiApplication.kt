package blue.starry.mitsubachi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import blue.starry.mitsubachi.core.domain.ApplicationScope
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import blue.starry.mitsubachi.feature.widget.photo.PhotoWidget
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.Firebase
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MitsubachiApplication : Application(), SingletonImageLoader.Factory, Configuration.Provider {
  @Suppress("LateinitUsage")
  @Inject
  lateinit var imageLoader: ImageLoader

  @Suppress("LateinitUsage")
  @Inject
  @ApplicationScope
  lateinit var applicationScope: CoroutineScope

  @Suppress("LateinitUsage")
  @Inject
  lateinit var applicationSettingsRepository: ApplicationSettingsRepository

  @Suppress("LateinitUsage")
  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun onCreate() {
    super.onCreate()

    applicationScope.launch {
      // 起動時にオプトアウトを設定
      Firebase.crashlytics.isCrashlyticsCollectionEnabled =
        applicationSettingsRepository.select { it.isFirebaseCrashlyticsEnabled }

      applicationSettingsRepository.flow
        .map { it.isFirebaseCrashlyticsEnabled }
        .distinctUntilChanged()
        .collect {
          Firebase.crashlytics.isCrashlyticsCollectionEnabled = it
        }
    }

    applicationScope.launch {
      PhotoWidget.updatePreview(applicationContext)
    }

    // staging variant でのみ Firebase App Distribution の更新チェックを実行
    if (BuildConfig.FLAVOR == "staging") {
      checkForAppUpdates()
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }

  private fun checkForAppUpdates() {
    FirebaseAppDistribution.getInstance()
      .updateIfNewReleaseAvailable()
      .addOnProgressListener { updateProgress ->
        Timber.d(
          "Firebase App Distribution update progress: ${updateProgress.apkBytesDownloaded}/${updateProgress.apkFileTotalBytes}",
        )
      }
      .addOnSuccessListener {
        Timber.d("Firebase App Distribution update check succeeded")
      }
      .addOnFailureListener { exception ->
        Timber.w(exception, "Firebase App Distribution update check failed")
      }
  }
}
