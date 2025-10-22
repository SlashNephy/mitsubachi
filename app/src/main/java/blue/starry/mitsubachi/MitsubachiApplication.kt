package blue.starry.mitsubachi

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MitsubachiApplication : Application(), SingletonImageLoader.Factory {
  @Suppress("LateinitUsage")
  @Inject
  lateinit var imageLoader: ImageLoader

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader {
    return imageLoader
  }
}
