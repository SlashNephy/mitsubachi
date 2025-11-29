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
import com.google.firebase.crashlytics.crashlytics
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
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }
}
