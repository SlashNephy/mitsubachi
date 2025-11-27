package blue.starry.mitsubachi.core.ui.compose.foundation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.compose.formatter.NumberFormatter
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols

@Composable
@Suppress("CognitiveComplexMethod")
fun LikeButton(
  isLiked: Boolean,
  likeCount: Int,
  onClickLike: () -> Unit,
  onClickUnlike: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier,
  ) {
    IconButton(
      onClick = if (isLiked) {
        onClickUnlike
      } else {
        onClickLike
      },
    ) {
      Icon(
        painter = painterResource(
          if (isLiked) {
            MaterialSymbols.favorite_filled
          } else {
            MaterialSymbols.favorite
          },
        ),
        contentDescription = if (isLiked) {
          stringResource(R.string.unlike_button)
        } else {
          stringResource(R.string.like_button)
        },
        tint = if (isLiked) {
          Color.Red
        } else {
          Color.Gray
        },
      )
    }

    if (likeCount > 0) {
      Text(
        text = NumberFormatter.formatIntWithShortNotation(likeCount),
        color = MaterialTheme.colorScheme.secondary,
      )
    }
  }
}

@Preview
@Composable
private fun LikeButtonPreview() {
  Column {
    LikeButton(
      isLiked = false,
      likeCount = 0,
      onClickLike = {},
      onClickUnlike = {},
    )

    LikeButton(
      isLiked = true,
      likeCount = 12_345,
      onClickLike = {},
      onClickUnlike = {},
    )
  }
}
