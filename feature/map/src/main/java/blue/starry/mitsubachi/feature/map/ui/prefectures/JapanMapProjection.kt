package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.math.cos
import kotlin.math.min

// 経度の縮尺を合わせる基準緯度。本州中央あたり。
private const val REFERENCE_LATITUDE = 36.0

// 本土を収める領域がキャンバスに占める割合。
private const val MAIN_AREA_RATIO = 0.98f

// 沖縄インセット枠の一辺がキャンバス短辺に占める割合。
private const val INSET_SIDE_RATIO = 0.26f

// インセット枠とキャンバス端の余白がキャンバス短辺に占める割合。
private const val INSET_MARGIN_RATIO = 0.02f

// インセット枠の内側に取る余白の割合。
private const val INSET_PADDING_RATIO = 0.08f

/**
 * 投影済みの 1 都道府県。
 *
 * [rings] はスクリーン座標。1 リングにつき `[x0, y0, x1, y1, ...]`。ヒットテストと Path 生成の両方に使う。
 */
class ProjectedPrefecture(
  val prefecture: Prefecture,
  val rings: List<FloatArray>,
) {
  // Path は android.graphics.Path に依存するため、ユニットテストで触らずに済むよう遅延生成する
  val path: Path by lazy {
    Path().apply {
      for (ring in rings) {
        var index = 0
        while (index < ring.size) {
          if (index == 0) moveTo(ring[0], ring[1]) else lineTo(ring[index], ring[index + 1])
          index += 2
        }
        close()
      }
    }
  }
}

/**
 * 都道府県ポリゴンをキャンバス座標に落とす。
 *
 * 沖縄県は本土から遠いので、同じ縮尺のまま左下のインセット枠に別途配置する。
 * 描画とヒットテストで同じ点列を使うため、判定と見た目がずれない。
 */
class JapanMapProjection(
  boundaries: List<PrefectureBoundary>,
  private val width: Float,
  private val height: Float,
) {
  private val longitudeScale = cos(Math.toRadians(REFERENCE_LATITUDE)).toFloat()
  private val shortSide = min(width, height)
  private val insetMargin = shortSide * INSET_MARGIN_RATIO
  private val insetSide = shortSide * INSET_SIDE_RATIO

  val insetBounds: Rect = Rect(
    offset = Offset(insetMargin, height - insetMargin - insetSide),
    size = Size(insetSide, insetSide),
  )

  val projected: List<ProjectedPrefecture>

  init {
    val (inset, main) = boundaries.partition { it.prefecture == Prefecture.Okinawa }

    val mainArea = Rect(
      offset = Offset(0f, 0f),
      size = Size(width * MAIN_AREA_RATIO, height * MAIN_AREA_RATIO),
    ).translate(width * (1 - MAIN_AREA_RATIO) / 2, height * (1 - MAIN_AREA_RATIO) / 2)

    val insetPadding = insetSide * INSET_PADDING_RATIO
    val insetArea = Rect(
      offset = Offset(insetBounds.left + insetPadding, insetBounds.top + insetPadding),
      size = Size(insetSide - insetPadding * 2, insetSide - insetPadding * 2),
    )

    projected = project(main, mainArea) + project(inset, insetArea)
  }

  fun hitTest(x: Float, y: Float): Prefecture? {
    for (item in projected) {
      for (ring in item.rings) {
        if (contains(ring, x, y)) {
          return item.prefecture
        }
      }
    }
    return null
  }

  private fun project(
    boundaries: List<PrefectureBoundary>,
    area: Rect,
  ): List<ProjectedPrefecture> {
    if (boundaries.isEmpty()) {
      return emptyList()
    }

    val bounds = computeProjectedBounds(boundaries)
    val sourceWidth = (bounds.maxX - bounds.minX).takeIf { it > 0f } ?: 1f
    val sourceHeight = (bounds.maxY - bounds.minY).takeIf { it > 0f } ?: 1f
    val scale = min(area.width / sourceWidth, area.height / sourceHeight)
    // アスペクト比を保ったまま領域の中央に置く
    val offsetX = area.left + (area.width - sourceWidth * scale) / 2
    val offsetY = area.top + (area.height - sourceHeight * scale) / 2

    return boundaries.map { boundary ->
      val rings = boundary.rings.map { ring ->
        toScreenRing(ring, bounds, scale, offsetX, offsetY)
      }
      ProjectedPrefecture(prefecture = boundary.prefecture, rings = rings)
    }
  }

  private fun toScreenRing(
    ring: List<DoubleArray>,
    bounds: ProjectedBounds,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
  ): FloatArray {
    val screen = FloatArray(ring.size * 2)
    ring.forEachIndexed { index, point ->
      screen[index * 2] = offsetX + (longitudeOf(point) - bounds.minX) * scale
      screen[index * 2 + 1] = offsetY + (latitudeOf(point) - bounds.minY) * scale
    }
    return screen
  }

  // 経度は基準緯度で縮め、緯度は北が上になるよう符号を反転させた中間座標を作る
  private fun longitudeOf(point: DoubleArray): Float = (point[0] * longitudeScale).toFloat()

  private fun latitudeOf(point: DoubleArray): Float = (-point[1]).toFloat()

  // PrefectureBoundary が既にリングごとの外接矩形 (west, south, east, north) を持つため、
  // 全点を走査せずそこから経度・緯度の外接範囲を求める
  private fun computeProjectedBounds(boundaries: List<PrefectureBoundary>): ProjectedBounds {
    val boxes = boundaries.flatMap { it.boundingBoxes }
    val scale = longitudeScale.toDouble()
    return ProjectedBounds(
      minX = (boxes.minOf { it[0] } * scale).toFloat(),
      maxX = (boxes.maxOf { it[2] } * scale).toFloat(),
      minY = (-boxes.maxOf { it[3] }).toFloat(),
      maxY = (-boxes.minOf { it[1] }).toFloat(),
    )
  }

  private fun contains(ring: FloatArray, x: Float, y: Float): Boolean {
    var inside = false
    val count = ring.size / 2
    var j = count - 1
    for (i in 0 until count) {
      val xi = ring[i * 2]
      val yi = ring[i * 2 + 1]
      val xj = ring[j * 2]
      val yj = ring[j * 2 + 1]
      if (yi > y != yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
        inside = !inside
      }
      j = i
    }
    return inside
  }

  /** 中間座標系（経度縮尺・緯度反転後）での外接矩形。 */
  private class ProjectedBounds(val minX: Float, val maxX: Float, val minY: Float, val maxY: Float)
}
