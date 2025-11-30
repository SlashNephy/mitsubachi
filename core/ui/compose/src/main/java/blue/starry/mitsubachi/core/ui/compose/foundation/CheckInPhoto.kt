package blue.starry.mitsubachi.core.ui.compose.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.Photo
import coil3.compose.AsyncImage

@Composable
fun CheckInPhoto(
  photo: Photo,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    contentAlignment = Alignment.Center,
  ) {
    AsyncImage(
      model = photo.url,
      contentDescription = null,
      modifier = Modifier
        .fillMaxSize()
        .clip(ShapeDefaults.Medium),
      contentScale = ContentScale.FillWidth,
    )
  }
}
