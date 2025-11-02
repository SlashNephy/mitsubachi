package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.primaryCategory
import blue.starry.mitsubachi.ui.formatter.VenueLocationFormatter
import blue.starry.mitsubachi.ui.rememberInterval
import coil3.compose.AsyncImage
import kotlin.time.Duration.Companion.seconds

@Composable
fun UserCheckInRow(
  checkIn: CheckIn,
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
  viewModel: UserCheckInsScreenViewModel = hiltViewModel(),
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
      url = checkIn.venue.primaryCategory?.iconUrl,
      contentDescription = checkIn.venue.primaryCategory?.name,
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
            includeCrossStreet = false,
          )
        }",
        color = Color.Gray,
      )

      val datetime = rememberInterval(10.seconds) {
        viewModel.formatPastDateTime(checkIn.timestamp)
      }
      Text(datetime)

      checkIn.message?.also {
        Spacer(modifier = Modifier.height(8.dp))
        Text(it)
      }
    }

    LikeIcon(
      isLiked = checkIn.isLiked,
      likeCount = checkIn.likeCount,
      onClick = {
        if (checkIn.isLiked) {
          viewModel.unlikeCheckIn(checkIn.id)
        } else {
          viewModel.likeCheckIn(checkIn.id)
        }
      },
      modifier = Modifier
        .size(48.dp)
        .padding(end = 16.dp),
    )
  }

  if (checkIn.photos.isNotEmpty()) {
    Photo(url = checkIn.photos.first().url)
  }
}

@Composable
private fun VenueCategoryIcon(
  url: String?,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
) {
  AsyncImage(
    model = url,
    contentDescription = contentDescription,
    modifier = modifier.aspectRatio(1f),
  )
}

@Composable
private fun LikeIcon(
  isLiked: Boolean,
  likeCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier,
  ) {
    IconButton(
      onClick = onClick,
    ) {
      Icon(
        imageVector = if (isLiked) {
          Icons.Filled.Favorite
        } else {
          Icons.Filled.FavoriteBorder
        },
        contentDescription = stringResource(R.string.like_button),
        tint = if (isLiked) Color.Red else Color.Gray,
      )
    }
    if (likeCount > 0) {
      Text(
        text = likeCount.toString(),
        color = Color.Gray,
      )
    }
  }
}
