package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.model.primaryCategory
import blue.starry.mitsubachi.ui.formatter.VenueLocationFormatter
import coil3.compose.AsyncImage

@Composable
fun VenueRow(
  venue: Venue,
  onClickVenue: (Venue) -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = {
        onClickVenue(venue)
      }),
  ) {
    val primaryCategory = venue.primaryCategory
    VenueCategoryIcon(
      model = primaryCategory?.iconUrl,
      contentDescription = primaryCategory?.name,
      modifier = Modifier
        .size(72.dp)
        .padding(horizontal = 8.dp, vertical = 4.dp),
    )

    Column(
      modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
    ) {
      Text(venue.name, fontWeight = FontWeight.Bold)
      Text(
        buildString {
          venue.location.distance?.let {
            append(VenueLocationFormatter.formatDistance(it))
            append('・')
          }
          append(VenueLocationFormatter.formatAddress(venue.location))
        },
        color = Color.Gray,
      )
    }
  }
}

@Composable
internal fun VenueCategoryIcon(
  model: Any?,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
) {
  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(CircleShape)
      .background(Color.Gray),
  ) {
    AsyncImage(
      model = model,
      contentDescription = contentDescription,
      modifier = Modifier
        .align(Alignment.Center)
        .fillMaxSize(0.7f),
    )
  }
}

@Preview
@Composable
private fun VenueCategoryPreview() {
  VenueCategoryIcon(
    model = "https://ss3.4sqi.net/img/categories_v2/arts_entertainment/aquarium_120.png",
    modifier = Modifier.width(100.dp),
  )
}
