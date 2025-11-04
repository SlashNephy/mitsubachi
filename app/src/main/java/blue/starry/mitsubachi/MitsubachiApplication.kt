package blue.starry.mitsubachi

import android.app.Application
import blue.starry.mitsubachi.domain.usecase.AppSettingsRepository
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
  lateinit var appSettingsRepository: AppSettingsRepository

  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  override fun onCreate() {
    applicationScope.launch {
      appSettingsRepository.isFirebaseCrashlyticsEnabled.collect { enabled ->
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = enabled
      }
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    super.onCreate()
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }
}
