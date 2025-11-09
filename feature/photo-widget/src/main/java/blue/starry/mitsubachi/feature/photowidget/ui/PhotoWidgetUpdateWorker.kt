package blue.starry.mitsubachi.feature.photowidget.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.Photo
import blue.starry.mitsubachi.domain.usecase.FetchUserCheckInsUseCase
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
  private val fetchUserCheckInsUseCase: FetchUserCheckInsUseCase,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
  private val imageLoader: ImageLoader,
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    return try {
      val glanceManager = GlanceAppWidgetManager(applicationContext)
      val glanceIds = glanceManager.getGlanceIds(PhotoWidget::class.java)

      // Check if user is logged in
      val account = findFoursquareAccountUseCase()
      if (account == null) {
        updateWidgetsNotLoggedIn(glanceIds)
        return Result.success()
      }

      // Fetch check-ins from user
      val checkIns = fetchUserCheckInsUseCase(userId = null, limit = 100, offset = 0)

      // Filter check-ins that have photos
      val checkInsWithPhotos = checkIns.filter { it.photos.isNotEmpty() }

      if (checkInsWithPhotos.isEmpty()) {
        updateWidgetsNoPhotos(glanceIds)
        return Result.success()
      }

      // Pick a random check-in with photo
      val randomCheckIn = checkInsWithPhotos.random()
      val randomPhoto = randomCheckIn.photos.random()
      Timber.d("PhotoWidgetUpdateWorker: Selected photo ${randomPhoto.id} from check-in ${randomCheckIn.id}")

      // Download and save photo
      val success = downloadAndSavePhoto(randomPhoto.url)
      if (!success) {
        Timber.w("PhotoWidgetUpdateWorker: Failed to load image bitmap")
        return Result.failure()
      }

      // Update all widgets with the new state
      updateWidgetsWithPhoto(glanceIds, randomCheckIn, randomPhoto)
      Result.success()
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

  private suspend fun updateWidgetsNotLoggedIn(glanceIds: List<GlanceId>) {
    Timber.d("PhotoWidgetUpdateWorker: No account found")
    glanceIds.forEach { glanceId ->
      updateAppWidgetState(applicationContext, PhotoWidgetStateDefinition, glanceId) {
        PhotoWidgetState(isLoggedIn = false, hasPhoto = false)
      }
    }
    PhotoWidget().updateAll(applicationContext)
  }

  private suspend fun updateWidgetsNoPhotos(glanceIds: List<GlanceId>) {
    Timber.d("PhotoWidgetUpdateWorker: No photos found")
    glanceIds.forEach { glanceId ->
      updateAppWidgetState(applicationContext, PhotoWidgetStateDefinition, glanceId) {
        PhotoWidgetState(isLoggedIn = true, hasPhoto = false)
      }
    }
    PhotoWidget().updateAll(applicationContext)
  }

  private suspend fun downloadAndSavePhoto(photoUrl: String): Boolean {
    val request = ImageRequest.Builder(applicationContext)
      .data(photoUrl)
      .memoryCachePolicy(CachePolicy.ENABLED)
      .diskCachePolicy(CachePolicy.ENABLED)
      .build()

    val imageResult = imageLoader.execute(request)
    val bitmap = imageResult.image?.toBitmap() ?: return false

    // Save bitmap to internal storage
    val file = File(applicationContext.filesDir, WIDGET_PHOTO_FILENAME)
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return true
  }

  private suspend fun updateWidgetsWithPhoto(
    glanceIds: List<GlanceId>,
    checkIn: CheckIn,
    photo: Photo,
  ) {
    val newState = PhotoWidgetState(
      photoId = photo.id,
      checkInId = checkIn.id,
      venueName = checkIn.venue.name,
      checkInDate = checkIn.timestamp,
      isLoggedIn = true,
      hasPhoto = true,
    )

    glanceIds.forEach { glanceId ->
      updateAppWidgetState(applicationContext, PhotoWidgetStateDefinition, glanceId) {
        newState
      }
    }
    PhotoWidget().updateAll(applicationContext)

    Timber.d("PhotoWidgetUpdateWorker: Successfully updated widget photo")
  }

  companion object {
    const val WIDGET_PHOTO_FILENAME = "widget_photo.jpg"
    const val WORK_NAME = "photo_widget_update"
  }
}
