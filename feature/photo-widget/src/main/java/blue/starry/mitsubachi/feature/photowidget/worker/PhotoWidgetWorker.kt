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
import blue.starry.mitsubachi.domain.model.foursquare.Photo
import blue.starry.mitsubachi.domain.usecase.FindFoursquareAccountUseCase
import blue.starry.mitsubachi.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.feature.photowidget.PhotoWidget
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetState
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetStateDefinition
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltWorker
class PhotoWidgetWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
  private val client: FoursquareApiClient,
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

    // TODO: 全件ランダムにしたい
    val photos = client.getUserPhotos(limit = 100)
    if (photos.isEmpty()) {
      widget.updateAll(glanceIds, PhotoWidgetState.NoPhotos)
      return Result.success()
    }

    val photo = photos.random()
    val file = download(photo) ?: return Result.failure()

    val newState = PhotoWidgetState.Photo(
      id = photo.id,
      path = file.absolutePath,
      checkInId = photo.checkInId,
      venueName = photo.venue.name,
      checkInAt = photo.createdAt,
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

  private suspend fun download(photo: Photo): File? {
    val request = ImageRequest.Builder(applicationContext)
      .data(photo.url)
      .build()

    val result = imageLoader.execute(request)
    val bitmap = result.image?.toBitmap() ?: return null

    val file = createCacheFile()
    FileOutputStream(file).use { stream ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    }
    return file
  }

  @OptIn(ExperimentalUuidApi::class)
  private fun createCacheFile(): File {
    val key = Uuid.random().toHexString()
    return File(applicationContext.cacheDir, "$key.jpg")
  }
}
