package blue.starry.mitsubachi.core.ui.compose.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.primaryCategory
import blue.starry.mitsubachi.core.ui.compose.formatter.LengthUnitFormatter
import blue.starry.mitsubachi.core.ui.compose.formatter.VenueLocationFormatter
import blue.starry.mitsubachi.core.ui.compose.locale.LocaleAware
import blue.starry.mitsubachi.core.ui.compose.preview.MockData

@Composable
fun VenueRow(
  venue: Venue,
  onClickVenue: (Venue) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        onClickVenue(venue)
      }
      .padding(horizontal = 8.dp, vertical = 4.dp),
  ) {
    VenueCategoryIcon(
      category = venue.primaryCategory,
      modifier = Modifier
        .size(72.dp),
    )

    Column(
      modifier = Modifier
        .fillMaxSize(),
    ) {
      Text(venue.name, fontWeight = FontWeight.Bold)
      Text(
        text = LocaleAware {
          buildString {
            venue.location.distance?.also {
              append(LengthUnitFormatter.formatMeters(it))
              append('・')
            }
            // TODO: より詳細な住所を表示する
            append(VenueLocationFormatter.formatAddress(venue.location))
          }
        },
        color = MaterialTheme.colorScheme.secondary,
      )
    }
  }
}

@Preview
@Composable
private fun VenueRowPreview() {
  VenueRow(
    venue = MockData.Venue,
    onClickVenue = {},
    modifier = Modifier.height(72.dp),
  )
}
