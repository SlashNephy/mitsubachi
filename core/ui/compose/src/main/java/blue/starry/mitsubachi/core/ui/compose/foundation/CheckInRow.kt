package blue.starry.mitsubachi.core.ui.compose.foundation

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.primaryCategory
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.compose.formatter.VenueLocationFormatter
import blue.starry.mitsubachi.core.ui.compose.locale.LocaleAware
import blue.starry.mitsubachi.core.ui.compose.preview.MockData
import blue.starry.mitsubachi.core.ui.compose.preview.PreviewImageProvider
import blue.starry.mitsubachi.core.ui.compose.rememberInterval
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

@Composable
fun CheckInRow(
  checkIn: CheckIn,
  formatDateTime: (ZonedDateTime) -> String,
  onClickCheckIn: () -> Unit,
  onClickLike: () -> Unit,
  onClickUnlike: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val checkInUser = checkIn.user ?: return

  Column(modifier = modifier.fillMaxWidth()) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      UserIcon(
        url = checkInUser.iconUrl,
        contentDescription = checkInUser.handle,
        modifier = Modifier
          .size(72.dp)
          .padding(horizontal = 8.dp, vertical = 4.dp),
      )

      Column(
        modifier = Modifier
          .fillMaxHeight()
          .weight(1f)
          .clickable(onClick = onClickCheckIn),
      ) {
        Text(checkInUser.displayName, color = MaterialTheme.colorScheme.secondary)
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

        val datetime = LocaleAware {
          rememberInterval(10.seconds) {
            formatDateTime(checkIn.timestamp)
          }
        }
        Text(datetime)

        checkIn.createdBy?.also {
          Text(stringResource(R.string.check_in_created_by, it.displayName))
        }

        checkIn.message?.also {
          Spacer(modifier = Modifier.height(8.dp))
          Text(it)
        }
      }

      LikeButton(
        isLiked = checkIn.isLiked,
        likeCount = checkIn.likeCount,
        onClickLike = onClickLike,
        onClickUnlike = onClickUnlike,
        modifier = Modifier
          .size(48.dp)
          .padding(end = 16.dp),
      )
    }

    if (checkIn.photos.isNotEmpty()) {
      // TODO: 複数画像のグリッドレイアウト
      CheckInPhoto(photo = checkIn.photos.first())
    }
  }
}

@Composable
@Preview
private fun CheckInRowPreview() {
  PreviewImageProvider {
    CheckInRow(
      checkIn = MockData.CheckIn,
      formatDateTime = { "10 分前" },
      onClickCheckIn = {},
      onClickLike = {},
      onClickUnlike = {},
    )
  }
}
