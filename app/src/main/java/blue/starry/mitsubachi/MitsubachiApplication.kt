package blue.starry.mitsubachi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import blue.starry.mitsubachi.domain.ApplicationScope
import blue.starry.mitsubachi.domain.usecase.ApplicationSettingsRepository
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
        applicationSettingsRepository.flow.map { it.isFirebaseCrashlyticsEnabled }.first()

      applicationSettingsRepository.flow
        .map { it.isFirebaseCrashlyticsEnabled }
        .distinctUntilChanged()
        .collect {
          Firebase.crashlytics.isCrashlyticsCollectionEnabled = it
        }
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }
}
