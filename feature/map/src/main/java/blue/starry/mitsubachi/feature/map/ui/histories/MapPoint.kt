package blue.starry.mitsubachi.feature.map.ui.histories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import blue.starry.mitsubachi.ui.MitsubachiColors

@Composable
internal fun MapPoint(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(MitsubachiColors.SwarmOrange),
  )
}
