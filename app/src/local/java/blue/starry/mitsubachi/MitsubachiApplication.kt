package blue.starry.mitsubachi

import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MitsubachiApplication : BaseMitsubachiApplication() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())

    Firebase.appCheck.installAppCheckProviderFactory(
      DebugAppCheckProviderFactory.getInstance(),
    )
  }
}
