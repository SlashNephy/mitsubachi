package blue.starry.mitsubachi

import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MitsubachiApplication : BaseMitsubachiApplication() {
  override fun onCreate() {
    super.onCreate()

    Firebase.appCheck.installAppCheckProviderFactory(
      PlayIntegrityAppCheckProviderFactory.getInstance(),
    )
  }
}
