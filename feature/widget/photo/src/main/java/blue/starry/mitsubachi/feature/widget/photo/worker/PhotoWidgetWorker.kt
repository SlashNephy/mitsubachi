package blue.starry.mitsubachi.feature.widget.photo.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import blue.starry.mitsubachi.core.domain.usecase.FindFoursquareAccountUseCase
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClient
import blue.starry.mitsubachi.core.domain.usecase.ImageDownloader
import blue.starry.mitsubachi.core.ui.common.deeplink.DeepLinkBuilder
import blue.starry.mitsubachi.feature.widget.photo.PhotoWidget
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetState
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetStateDefinition
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.io.path.absolutePathString

@HiltWorker
class PhotoWidgetWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
  private val client: FoursquareApiClient,
  private val imageDownloader: ImageDownloader,
  private val deepLinkBuilder: DeepLinkBuilder,
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
    val imagePath = imageDownloader.download(photo.url) ?: return Result.failure()

    // TODO: VenueLocationFormatter を利用する
    val venueAddress = photo.venue.location.crossStreet
      ?: photo.venue.location.address ?: photo.venue.location.city ?: photo.venue.location.country

    val newState = PhotoWidgetState.Photo(
      id = photo.id,
      path = imagePath.absolutePathString(),
      checkInUri = deepLinkBuilder.buildCheckInLink(photo.checkInId),
      venueName = photo.venue.name,
      venueAddress = venueAddress,
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
}
