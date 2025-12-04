package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.primaryCategory
import blue.starry.mitsubachi.core.ui.compose.formatter.VenueLocationFormatter
import blue.starry.mitsubachi.core.ui.compose.foundation.CheckInPhoto
import blue.starry.mitsubachi.core.ui.compose.foundation.LikeButton
import blue.starry.mitsubachi.core.ui.compose.foundation.VenueCategoryIcon
import blue.starry.mitsubachi.core.ui.compose.rememberInterval
import kotlin.time.Duration.Companion.seconds

@Composable
fun UserCheckInRow(
  checkIn: CheckIn,
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: UserCheckInsScreenViewModel = hiltViewModel(),
) {
  Column(
    modifier = modifier.fillMaxWidth(),
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .clickable {
          onClickCheckIn(checkIn)
        },
    ) {
      VenueCategoryIcon(
        category = checkIn.venue.primaryCategory,
        modifier = Modifier
          .size(72.dp)
          .padding(horizontal = 8.dp, vertical = 4.dp),
      )

      Column(
        modifier = Modifier
          .fillMaxHeight()
          .weight(1f),
      ) {
        Text(checkIn.venue.name, fontWeight = FontWeight.Bold)
        Text(
          "${checkIn.venue.primaryCategory?.name}・${
            VenueLocationFormatter.formatAddress(
              checkIn.venue.location,
              withCrossStreet = false,
            )
          }",
          color = MaterialTheme.colorScheme.secondary,
        )

        val datetime = rememberInterval(10.seconds) {
          viewModel.formatAsRelativeTimeSpan(checkIn.timestamp)
        }
        Text(datetime)

        checkIn.message?.also {
          Spacer(modifier = Modifier.height(8.dp))
          Text(it)
        }
      }

      LikeButton(
        isLiked = checkIn.isLiked,
        likeCount = checkIn.likeCount,
        onClickLike = { viewModel.likeCheckIn(checkIn.id) },
        onClickUnlike = { viewModel.unlikeCheckIn(checkIn.id) },
        modifier = Modifier
          .size(48.dp)
          .padding(end = 16.dp),
      )
    }

    if (checkIn.photos.isNotEmpty()) {
      CheckInPhoto(photo = checkIn.photos.first())
    }
  }
}
