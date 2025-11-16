package blue.starry.mitsubachi.core.ui.compose.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.drawable.toBitmap
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.compose.preview.PreviewImageProvider
import coil3.compose.AsyncImage
import kotlin.math.roundToInt

@Composable
fun UserIcon(
  url: String,
  contentDescription: String?,
  modifier: Modifier = Modifier,
) {
  val resources = LocalResources.current
  val maskBitmap = remember {
    resources.getDrawable(R.drawable.swarm_avatar_mask, null).toBitmap().asImageBitmap()
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
            width = size.width.roundToInt(),
            height = size.height.roundToInt(),
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
  PreviewImageProvider {
    UserIcon(url = "", contentDescription = null)
  }
}
