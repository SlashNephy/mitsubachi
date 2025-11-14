package blue.starry.mitsubachi.feature.widget.photo.ui

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import blue.starry.mitsubachi.feature.widget.photo.R
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetState
import java.io.File
import java.time.ZonedDateTime

@Composable
internal fun PhotoContent(photo: PhotoWidgetState.Photo) {
  val bitmap = remember {
    val file = File(photo.path)
    if (file.exists()) {
      BitmapFactory.decodeFile(file.absolutePath)
    } else {
      null
    }
  }

  if (bitmap != null) {
    ActualPhotoContent(photo, ImageProvider(bitmap))
  } else {
    NoPhotosContent()
  }
}

@Composable
private fun ActualPhotoContent(state: PhotoWidgetState.Photo, image: ImageProvider) {
  Box(
    modifier = GlanceModifier.fillMaxSize(),
  ) {
    Image(
      provider = image,
      contentDescription = state.venueName,
      modifier = GlanceModifier
        .fillMaxSize()
        .clickable(
          actionStartActivity(
            Intent(
              Intent.ACTION_VIEW,
              "blue.starry.mitsubachi://checkin/${state.checkInId}".toUri(), // TODO: チェックイン詳細画面が開けるようにする
            ).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
          ),
        ),
      contentScale = ContentScale.Crop,
    )

    PhotoOverlay(state)
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun ActualPhotoContentPreview() {
  ActualPhotoContent(
    state = PhotoWidgetState.Photo(
      id = "photo",
      path = "photo.jpg",
      checkInId = "check_in",
      venueName = "ぷれびゅーパーク",
      venueAddress = "東京都渋谷区",
      checkInAt = ZonedDateTime.now().minusDays(7),
    ),
    image = ImageProvider(R.drawable.sushi),
  )
}
