package blue.starry.mitsubachi

import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MitsubachiApplication : BaseMitsubachiApplication() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())
  }
}
