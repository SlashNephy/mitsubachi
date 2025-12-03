package blue.starry.mitsubachi.feature.checkin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.primaryCategory
import blue.starry.mitsubachi.core.ui.compose.formatter.VenueLocationFormatter
import blue.starry.mitsubachi.core.ui.compose.foundation.CheckInRow
import blue.starry.mitsubachi.core.ui.compose.foundation.UserIcon
import blue.starry.mitsubachi.core.ui.compose.foundation.VenueCategoryIcon
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.checkin.R

@Composable
fun CheckInDetailScreen(
  checkIn: CheckIn,
  viewModel: CheckInDetailScreenViewModel = hiltViewModel(),
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .background(MaterialTheme.colorScheme.background),
  ) {
    // Venue Card
    VenueCard(checkIn = checkIn)

    Spacer(modifier = Modifier.height(16.dp))

    CheckInRow(
      checkIn = checkIn,
      formatDateTime = { viewModel.formatAsRelativeDateTime(checkIn.timestamp) },
      onClickLike = { viewModel.likeCheckIn(checkIn.id) },
      onClickUnlike = { viewModel.unlikeCheckIn() },
      onClickCheckIn = {},
    )

    // Friends Here
    if (checkIn.friendsHere.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      FriendsHereSection(friends = checkIn.friendsHere)
    }

    // Coins
    if (checkIn.coin > 0) {
      Spacer(modifier = Modifier.height(16.dp))
      CoinSection(coins = checkIn.coin)
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}

@Composable
@Suppress("LongMethod")
private fun VenueCard(checkIn: CheckIn) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(16.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        VenueCategoryIcon(
          category = checkIn.venue.primaryCategory,
          modifier = Modifier
            .size(72.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = checkIn.venue.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = checkIn.venue.primaryCategory?.name.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
          )
          Text(
            text = VenueLocationFormatter.formatAddress(checkIn.venue.location),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
          )
        }
      }

      // Score Badge
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(Color(0xFF9ACD32)),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "7.4", // TODO: Get actual score from venue
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun FriendsHereSection(friends: List<FoursquareUser>) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
  ) {
    Text(
      text = pluralStringResource(R.plurals.friends_here, friends.size, friends.size),
      style = MaterialTheme.typography.bodyMedium,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      friends.take(10).forEach { friend ->
        UserIcon(
          url = friend.iconUrl,
          contentDescription = friend.handle,
          modifier = Modifier.size(48.dp),
        )
      }
    }
  }
}

@Composable
private fun CoinSection(coins: Int) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(Color(0xFFFFD700)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        painter = painterResource(MaterialSymbols.star_filled),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(20.dp),
      )
    }

    Text(
      text = stringResource(R.string.coins, coins),
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}
