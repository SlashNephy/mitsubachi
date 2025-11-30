package blue.starry.mitsubachi

import com.google.firebase.appdistribution.FirebaseAppDistribution
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseMainActivity() {
  override fun onResume() {
    super.onResume()

    checkFirebaseAppDistributionNewRelease()
  }

  private fun checkFirebaseAppDistributionNewRelease() {
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
