package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.domain.model.FoursquareUser
import blue.starry.mitsubachi.domain.model.Photo
import blue.starry.mitsubachi.domain.model.Source
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.model.VenueCategory
import blue.starry.mitsubachi.domain.model.VenueLocation
import blue.starry.mitsubachi.domain.model.primaryCategory
import blue.starry.mitsubachi.ui.formatter.VenueLocationFormatter
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
@Suppress("LongMethod") // TODO: リファクタリング
fun CheckInRow(
  checkIn: CheckIn,
  onClickCheckIn: (checkInId: String) -> Unit,
  viewModel: HomeScreenViewModel = hiltViewModel(),
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        onClickCheckIn(checkIn.id)
      },
  ) {
    UserIcon(
      url = checkIn.user.iconUrl,
      contentDescription = checkIn.user.handle,
      modifier = Modifier
        .size(72.dp)
        .padding(horizontal = 8.dp, vertical = 4.dp),
    )

    Column(
      modifier = Modifier
        .fillMaxHeight()
        .weight(1f),
    ) {
      Text(checkIn.user.displayName, color = Color.Gray)
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

      // なんかいまいち
      var datetime by remember { mutableStateOf("") }
      LaunchedEffect(checkIn.timestamp) {
        while (true) {
          datetime = viewModel.formatPastDateTime(checkIn.timestamp)
          delay(10.seconds)
        }
      }
      if (datetime.isNotEmpty()) {
        Text(datetime)
      }

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
    // TODO: 複数画像のグリッドレイアウト
    Photo(url = checkIn.photos.first().url)
  }
}

@Composable
fun Photo(url: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    contentAlignment = Alignment.Center,
  ) {
    AsyncImage(
      model = url,
      contentDescription = null,
      modifier = Modifier
        .fillMaxSize()
        .clip(ShapeDefaults.Medium),
      contentScale = ContentScale.FillWidth,
    )
  }
}

@Composable
@Preview
private fun CheckInRowPreview() {
  CheckInRow(
    checkIn = CheckIn(
      id = "id",
      venue = Venue(
        id = "id",
        name = "スターバックスコーヒー 丸の内ビル店",
        categories = listOf(
          VenueCategory(
            id = "4bf58dd8d48988d1e0931735",
            name = "コーヒーショップ",
            iconUrl = "https://ss3.4sqi.net/img/categories_v2/food/coffeeshop_120.png",
            isPrimary = true,
          ),
        ),
        location = VenueLocation(
          latitude = 35.681236,
          longitude = 139.767125,
          distance = null,
          countryCode = "JP",
          country = "日本",
          postalCode = "100-6390",
          state = "東京都",
          city = "千代田区",
          address = "丸の内2-4-1 ",
          crossStreet = "丸の内ビルディング B1F",
          neighborhood = "丸の内",
        ),
        createdAt = ZonedDateTime.now().minusDays(1),
      ),
      user = FoursquareUser(
        id = "id",
        handle = "handle",
        firstName = "太郎",
        displayName = "山田太郎",
        iconUrl = "https://fastly.4sqi.net/img/user/original/169641728_E4167jqJ_ZVweL-ylJRwFG7qhxdmD-IwwmQ-00B9IFxOTHmW2LggCyyzpq9fiDCAPGFDyFIIF.jpg",
        countryCode = "JP",
        state = "東京都",
        city = "千代田区",
        address = "丸の内2-4-1",
        gender = null,
        isPrivateProfile = false,
      ),
      coin = 0,
      sticker = null,
      message = "今日は仕事が早く終わったので、久しぶりにスタバでコーヒーを飲んでリフレッシュしました！新しい季節のフレーバーも試してみたけど、美味しかったです。皆さんもぜひ訪れてみてください！",
      photos = listOf(
        Photo(
          id = "id",
          url = "https://example.com/photo.png",
          width = 1080,
          height = 1080,
        ),
      ),
      timestamp = ZonedDateTime.now(),
      isLiked = true,
      likeCount = 5,
      source = Source(name = "Swarm for iOS", url = "https://www.swarmapp.com"),
      isMeyer = true,
    ),
    onClickCheckIn = {},
  )
}

@Composable
private fun UserIcon(
  url: String,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
) {
  // TODO: 再利用
  val maskDrawable =
    checkNotNull(ContextCompat.getDrawable(LocalContext.current, R.drawable.swarm_avatar_mask))
  val maskBitmap = remember {
    maskDrawable.toBitmap().asImageBitmap()
  }

  Box(
    modifier = modifier
      .aspectRatio(1f)
      .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
      .drawWithContent {
        drawContent()
        drawImage(
          image = maskBitmap,
          blendMode = BlendMode.DstIn,
          dstSize = IntSize(
            width = this.size.width.roundToInt(),
            height = this.size.height.roundToInt(),
          ),
        )
      },
  ) {
    AsyncImage(
      model = url,
      contentDescription = contentDescription,
      modifier = Modifier.fillMaxSize(),
    )
  }
}

@Preview
@Composable
private fun UserIconPreview() {
  UserIcon(
    url = "https://fastly.4sqi.net/img/user/original/169641728_E4167jqJ_ZVweL-ylJRwFG7qhxdmD-IwwmQ-00B9IFxOTHmW2LggCyyzpq9fiDCAPGFDyFIIF.jpg",
    modifier = Modifier
      .size(100.dp)
      .border(1.dp, Color.Black),
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

@Preview
@Composable
private fun BorderLikeIconPreview() {
  LikeIcon(
    isLiked = false,
    likeCount = 10,
    onClick = {},
    modifier = Modifier.size(50.dp),
  )
}

@Preview
@Composable
private fun LikeIconPreview() {
  LikeIcon(
    isLiked = true,
    likeCount = 42,
    onClick = {},
    modifier = Modifier.size(50.dp),
  )
}
