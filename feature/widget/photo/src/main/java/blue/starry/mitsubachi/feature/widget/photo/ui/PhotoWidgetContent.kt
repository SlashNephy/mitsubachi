package blue.starry.mitsubachi.feature.widget.photo.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import blue.starry.mitsubachi.core.ui.glance.Material3CircularProgressIndicator
import blue.starry.mitsubachi.core.ui.glance.image.localImageProvider
import blue.starry.mitsubachi.core.ui.glance.withAlpha
import blue.starry.mitsubachi.feature.widget.photo.R
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetState

@Composable
internal fun PhotoWidgetContent(state: PhotoWidgetState) {
  Scaffold(horizontalPadding = 0.dp) {
    when (state) {
      is PhotoWidgetState.Loading -> Loading()
      is PhotoWidgetState.Photo -> Photo(state)
      is PhotoWidgetState.NoPhotos -> NoPhotos()
      is PhotoWidgetState.LoginRequired -> LoginRequired()
    }
  }
}

@Composable
private fun Loading() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = GlanceModifier.fillMaxSize(),
  ) {
    Material3CircularProgressIndicator()
  }
}

@Composable
private fun Photo(photo: PhotoWidgetState.Photo) {
  val image = when (photo.image) {
    is PhotoWidgetState.Photo.Image.Local -> localImageProvider(photo.image.path)
    is PhotoWidgetState.Photo.Image.Resource -> ImageProvider(photo.image.id)
  }

  Box(modifier = GlanceModifier.fillMaxSize()) {
    image?.also {
      Image(
        provider = it,
        contentDescription = photo.venueName,
        modifier = GlanceModifier
          .fillMaxSize()
          .clickable(
            onClick = actionStartActivity(
              Intent(Intent.ACTION_VIEW, photo.checkInUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
              },
            ),
          ),
        contentScale = ContentScale.Crop,
      )
    }

    PhotoOverlay(photo)
  }
}

@Composable
private fun PhotoOverlay(photo: PhotoWidgetState.Photo) {
  val context = LocalContext.current
  val surfaceColor = GlanceTheme.colors.surface.withAlpha(context, 0.7f)

  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(12.dp),
    contentAlignment = Alignment.BottomStart,
  ) {
    Column(
      modifier = GlanceModifier
        .background(surfaceColor)
        .cornerRadius(8.dp)
        .padding(8.dp),
    ) {
      Text(
        text = photo.venueName,
        style = TextDefaults.defaultTextStyle.copy(
          color = GlanceTheme.colors.onSurface,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
        ),
        maxLines = 1,
      )

      Text(
        text = photo.venueAddress,
        style = TextDefaults.defaultTextStyle.copy(
          color = GlanceTheme.colors.onSurface,
          fontSize = 10.sp,
        ),
        maxLines = 1,
      )

      Text(
        text = photo.date,
        style = TextDefaults.defaultTextStyle.copy(
          color = GlanceTheme.colors.onSurfaceVariant,
          fontSize = 10.sp,
        ),
        maxLines = 1,
      )
    }
  }
}

@Composable
private fun NoPhotos() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalAlignment = Alignment.CenterVertically,
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    val context = LocalContext.current

    Text(
      text = context.getString(R.string.no_photos_title),
      style = TextDefaults.defaultTextStyle.copy(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )

    Spacer(modifier = GlanceModifier.height(16.dp))

    Text(
      text = context.getString(R.string.no_photos_description),
      style = TextDefaults.defaultTextStyle.copy(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}

@Composable
private fun LoginRequired() {
  val context = LocalContext.current

  Column(
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = GlanceModifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    Text(
      text = context.getString(R.string.login_required_title),
      style = TextDefaults.defaultTextStyle.copy(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )

    Spacer(modifier = GlanceModifier.height(16.dp))

    Text(
      text = context.getString(R.string.login_required_description),
      style = TextDefaults.defaultTextStyle.copy(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 200)
@Composable
private fun LoadingPreview() {
  PhotoWidgetContent(state = PhotoWidgetState.Loading)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 200)
@Composable
private fun PhotoPreview() {
  PhotoWidgetContent(
    state = PhotoWidgetState.Photo(
      id = "photo",
      image = PhotoWidgetState.Photo.Image.Resource(R.drawable.sushi),
      checkInUri = "app://check_in/123".toUri(),
      venueName = "ぷれびゅーパーク",
      venueAddress = "東京都渋谷区",
      date = "2025/11/01",
    ),
  )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 200)
@Composable
private fun NoPhotosPreview() {
  PhotoWidgetContent(state = PhotoWidgetState.NoPhotos)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 200)
@Composable
private fun LoginRequiredPreview() {
  PhotoWidgetContent(state = PhotoWidgetState.LoginRequired)
}
