package blue.starry.mitsubachi.feature.widget.photo

import androidx.core.net.toUri
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetState

internal val previewState = PhotoWidgetState.Photo(
  id = "photo",
  image = PhotoWidgetState.Photo.Image.Resource(R.drawable.sushi),
  checkInUri = "invalid://invalid".toUri(),
  venueName = "ぷれびゅーパーク",
  venueAddress = "東京都渋谷区",
  date = "2025/11/1",
)
