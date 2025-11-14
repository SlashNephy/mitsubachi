package blue.starry.mitsubachi.core.ui.compose.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.VenueCategory
import blue.starry.mitsubachi.core.ui.common.MitsubachiColors
import blue.starry.mitsubachi.core.ui.compose.preview.MockData
import blue.starry.mitsubachi.core.ui.compose.preview.PreviewImageProvider
import coil3.compose.AsyncImage

@Composable
fun VenueCategoryIcon(
  category: VenueCategory?,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(CircleShape)
      .background(MitsubachiColors.SwarmOrange),
  ) {
    if (category != null) {
      AsyncImage(
        model = category.iconUrl,
        contentDescription = category.name,
        modifier = Modifier
          .align(Alignment.Center)
          .fillMaxSize(0.6f),
      )
    }
  }
}

@Preview
@Composable
private fun VenueCategoryIconPreview() {
  PreviewImageProvider {
    VenueCategoryIcon(
      category = MockData.PrimaryVenueCategory,
      modifier = Modifier.width(72.dp),
    )
  }
}
