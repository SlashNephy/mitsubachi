package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

// 日本列島の外接矩形はおおむね縦長。幅に対する高さの比
private const val MAP_ASPECT_RATIO = 0.82f

@Composable
fun PrefectureMap(
  boundaries: ImmutableList<PrefectureBoundary>,
  levels: ImmutableMap<Prefecture, PrefectureLevel>,
  selected: Prefecture?,
  onSelect: (Prefecture) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  val density = LocalDensity.current

  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(MAP_ASPECT_RATIO),
  ) {
    val widthPx = with(density) { maxWidth.toPx() }
    val heightPx = with(density) { maxHeight.toPx() }

    // 投影は composition のたびに作り直さない
    val projection = remember(boundaries, widthPx, heightPx) {
      JapanMapProjection(boundaries, widthPx, heightPx)
    }

    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(projection) {
          detectTapGestures { offset ->
            projection.hitTest(offset.x, offset.y)?.also(onSelect)
          }
        },
    ) {
      drawPrefectures(projection, levels, selected, colorScheme)
      drawInsetBorder(projection, colorScheme)
    }
  }
}

private fun DrawScope.drawPrefectures(
  projection: JapanMapProjection,
  levels: ImmutableMap<Prefecture, PrefectureLevel>,
  selected: Prefecture?,
  colorScheme: ColorScheme,
) {
  for (item in projection.projected) {
    val level = levels[item.prefecture] ?: PrefectureLevel.Unvisited
    drawPath(item.path, colorScheme.prefectureLevelColor(level))
    // 同じレベルの隣接県が塊に見えないよう境界線は常に引く
    drawPath(
      path = item.path,
      color = colorScheme.outlineVariant,
      style = Stroke(width = 1.dp.toPx()),
    )
  }

  // 選択中の都道府県は塗りではなくアウトラインの太さで示す
  selected?.also { prefecture ->
    projection.projected.firstOrNull { it.prefecture == prefecture }?.also {
      drawPath(
        path = it.path,
        color = colorScheme.onSurface,
        style = Stroke(width = 3.dp.toPx()),
      )
    }
  }
}

private fun DrawScope.drawInsetBorder(projection: JapanMapProjection, colorScheme: ColorScheme) {
  drawRect(
    color = colorScheme.outlineVariant,
    topLeft = projection.insetBounds.topLeft,
    size = projection.insetBounds.size,
    style = Stroke(width = 1.dp.toPx()),
  )
}
