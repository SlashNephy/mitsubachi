package blue.starry.mitsubachi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import blue.starry.mitsubachi.core.domain.ApplicationScope
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import blue.starry.mitsubachi.core.domain.usecase.LocationTrackingRepository
import blue.starry.mitsubachi.feature.widget.photo.PhotoWidget
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.Firebase
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LateinitUsage")
abstract class BaseMitsubachiApplication :
  Application(),
  SingletonImageLoader.Factory,
  Configuration.Provider {
  @Inject
  lateinit var imageLoader: ImageLoader

  @Inject
  @ApplicationScope
  lateinit var applicationScope: CoroutineScope

  @Inject
  lateinit var applicationSettingsRepository: ApplicationSettingsRepository

  @Inject
  lateinit var locationTrackingRepository: LocationTrackingRepository

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  @Inject
  lateinit var firebaseAppCheckProviderFactory: AppCheckProviderFactory

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
      // バックグラウンド位置情報追跡の設定を監視
      applicationSettingsRepository.flow
        .map { it.isBackgroundLocationTrackingEnabled }
        .distinctUntilChanged()
        .collect { isEnabled ->
          if (isEnabled) {
            locationTrackingRepository.startTracking()
          } else {
            locationTrackingRepository.stopTracking()
          }
        }
    }

    applicationScope.launch {
      PhotoWidget.updatePreview(applicationContext)
    }

    Firebase.initialize(this)
    Firebase.appCheck.installAppCheckProviderFactory(firebaseAppCheckProviderFactory)
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }
}
