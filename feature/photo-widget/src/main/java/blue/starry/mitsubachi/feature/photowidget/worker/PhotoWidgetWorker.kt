package blue.starry.mitsubachi.feature.photowidget.worker

import android.content.Context
import android.graphics.Bitmap
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import blue.starry.mitsubachi.domain.usecase.FetchUserCheckInsUseCase
import blue.starry.mitsubachi.domain.usecase.FindFoursquareAccountUseCase
import blue.starry.mitsubachi.feature.photowidget.PhotoWidget
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetState
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetStateDefinition
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.toBitmap
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

@HiltWorker
class PhotoWidgetWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val fetchUserCheckInsUseCase: FetchUserCheckInsUseCase,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
  private val imageLoader: ImageLoader,
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    val glanceManager = GlanceAppWidgetManager(applicationContext)
    val widget = PhotoWidget()
    val glanceIds = glanceManager.getGlanceIds(widget::class.java)

    val account = findFoursquareAccountUseCase()
    if (account == null) {
      widget.updateAll(glanceIds, PhotoWidgetState.LoginRequired)
      return Result.success()
    }

    // TODO: 画像一覧を取得するエンドポイントに切り替える
    val checkIns = fetchUserCheckInsUseCase(limit = 100)
    val checkInsWithPhotos = checkIns.filter { it.photos.isNotEmpty() }
    if (checkInsWithPhotos.isEmpty()) {
      widget.updateAll(glanceIds, PhotoWidgetState.NoPhotos)
      return Result.success()
    }

    val randomCheckIn = checkInsWithPhotos.random()
    val randomPhoto = randomCheckIn.photos.random()
    val success = downloadAndSavePhoto(randomPhoto.url)
    if (!success) {
      Timber.w("PhotoWidgetUpdateWorker: Failed to load image bitmap")
      return Result.failure()
    }

    val newState = PhotoWidgetState.Photo(
      id = randomPhoto.id,
      checkInId = randomCheckIn.id,
      venueName = randomCheckIn.venue.name,
      checkInAt = randomCheckIn.timestamp,
    )
    widget.updateAll(glanceIds, newState)
    return Result.success()
  }

  private suspend fun PhotoWidget.updateAll(glanceIds: List<GlanceId>, newState: PhotoWidgetState) {
    coroutineScope {
      glanceIds.map { id ->
        launch {
          updateAppWidgetState(applicationContext, PhotoWidgetStateDefinition, id) {
            newState
          }
        }
      }
    }.joinAll()

    updateAll(applicationContext)
  }

  private suspend fun downloadAndSavePhoto(photoUrl: String): Boolean {
    val request = ImageRequest.Builder(applicationContext)
      .data(photoUrl)
      .memoryCachePolicy(CachePolicy.ENABLED)
      .diskCachePolicy(CachePolicy.ENABLED)
      .build()

    val imageResult = imageLoader.execute(request)
    val bitmap = imageResult.image?.toBitmap() ?: return false

    val file = File(applicationContext.filesDir, WIDGET_PHOTO_FILENAME)
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return true
  }

  companion object {
    const val WIDGET_PHOTO_FILENAME = "widget_photo.jpg"
  }
}
