package blue.starry.mitsubachi.ui.feature.photowidget

import android.content.Context
import android.graphics.Bitmap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import blue.starry.mitsubachi.domain.usecase.FetchUserCheckInPhotosUseCase
import blue.starry.mitsubachi.domain.usecase.FindFoursquareAccountUseCase
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.toBitmap
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * Worker to download and cache a random photo from user's check-ins
 */
@OptIn(ExperimentalCoilApi::class)
@HiltWorker
class PhotoWidgetUpdateWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val fetchUserCheckInPhotosUseCase: FetchUserCheckInPhotosUseCase,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
  private val imageLoader: ImageLoader,
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    return try {
      // Check if user is logged in
      val account = findFoursquareAccountUseCase()
      if (account == null) {
        Timber.d("PhotoWidgetUpdateWorker: No account found, skipping update")
        return Result.success()
      }

      // Fetch photos from user's check-ins
      val photosResult = fetchUserCheckInPhotosUseCase(limit = 100, offset = 0)
      val photos = photosResult.getOrNull()

      if (photos.isNullOrEmpty()) {
        Timber.d("PhotoWidgetUpdateWorker: No photos found")
        return Result.success()
      }

      // Pick a random photo
      val randomPhoto = photos.random()
      Timber.d("PhotoWidgetUpdateWorker: Selected photo ${randomPhoto.id}")

      // Download the photo and save it to internal storage
      val request = ImageRequest.Builder(applicationContext)
        .data(randomPhoto.url)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()

      val imageResult = imageLoader.execute(request)
      val bitmap = imageResult.image?.toBitmap()

      if (bitmap != null) {
        // Save bitmap to internal storage
        val file = File(applicationContext.filesDir, WIDGET_PHOTO_FILENAME)
        FileOutputStream(file).use { out ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        // Store the selected photo metadata in preferences
        val prefs = applicationContext.getSharedPreferences(
          WIDGET_PREFS_NAME,
          Context.MODE_PRIVATE,
        )
        prefs.edit()
          .putString(PREF_KEY_PHOTO_ID, randomPhoto.id)
          .putInt(PREF_KEY_PHOTO_WIDTH, randomPhoto.width)
          .putInt(PREF_KEY_PHOTO_HEIGHT, randomPhoto.height)
          .putBoolean(PREF_KEY_PHOTO_AVAILABLE, true)
          .apply()

        Timber.d("PhotoWidgetUpdateWorker: Successfully updated widget photo")
        Result.success()
      } else {
        Timber.w("PhotoWidgetUpdateWorker: Failed to load image bitmap")
        Result.failure()
      }
    } catch (e: IllegalStateException) {
      Timber.e(e, "PhotoWidgetUpdateWorker: Failed to update widget due to invalid state")
      Result.failure()
    } catch (e: SecurityException) {
      Timber.e(e, "PhotoWidgetUpdateWorker: Failed to update widget due to security exception")
      Result.failure()
    } catch (e: java.io.IOException) {
      Timber.e(e, "PhotoWidgetUpdateWorker: Failed to save image file")
      Result.failure()
    }
  }

  companion object {
    const val WIDGET_PREFS_NAME = "photo_widget_prefs"
    const val PREF_KEY_PHOTO_ID = "photo_id"
    const val PREF_KEY_PHOTO_WIDTH = "photo_width"
    const val PREF_KEY_PHOTO_HEIGHT = "photo_height"
    const val PREF_KEY_PHOTO_AVAILABLE = "photo_available"
    const val WIDGET_PHOTO_FILENAME = "widget_photo.jpg"
    const val WORK_NAME = "photo_widget_update"
  }
}
