package blue.starry.mitsubachi

import android.app.Application
import blue.starry.mitsubachi.domain.ApplicationScope
import blue.starry.mitsubachi.domain.usecase.AppSettingsRepository
import blue.starry.mitsubachi.ui.feature.photowidget.PhotoWidgetWorkScheduler
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MitsubachiApplication : Application(), SingletonImageLoader.Factory {
  @Suppress("LateinitUsage")
  @Inject
  lateinit var imageLoader: ImageLoader

  @Suppress("LateinitUsage")
  @Inject
  @ApplicationScope
  lateinit var applicationScope: CoroutineScope

  @Suppress("LateinitUsage")
  @Inject
  lateinit var appSettingsRepository: AppSettingsRepository

  @Suppress("LateinitUsage")
  @Inject
  lateinit var photoWidgetWorkScheduler: PhotoWidgetWorkScheduler

  override fun onCreate() {
    super.onCreate()

    applicationScope.launch {
      // 起動時にオプトアウトを設定
      Firebase.crashlytics.isCrashlyticsCollectionEnabled =
        appSettingsRepository.isFirebaseCrashlyticsEnabled.first()

      appSettingsRepository.isFirebaseCrashlyticsEnabled.collect { enabled ->
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = enabled
      }
    }

    // Schedule periodic photo widget updates
    photoWidgetWorkScheduler.schedulePeriodicUpdate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }
}
