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

// 本土として描画する地理窓。アセット (Natural Earth 1:10m) の全リングの外接矩形を実測し、
// 遠隔離島と本土のあいだに空いている値の中央を境目に取った。
// 南限 30.1: トカラ列島の北端 30.005N (鹿児島県) と屋久島の南端 30.226N のあいだ。
//   これで小笠原諸島 (26.6-27.2N)・硫黄島 (24.7-25.5N)・南鳥島 (24.3N) も落ちる。
// 東限 151.4: 択捉島の東端 148.856E (北海道) と南鳥島 153.941E (東京都) のあいだ。
private val MAIN_WINDOW = GeoWindow(west = 120.0, south = 30.1, east = 151.4, north = 46.0)

// 薩南諸島 (トカラ列島・奄美群島) を描画する地理窓。いずれも鹿児島県で、九州の南に連なる。
// 経度が近い東京都の離島 (硫黄島 141.275E・小笠原諸島 142.111E) とは 11 度以上離れているため、
// 東限 130.7 (喜界島の東端 130.030E のすぐ東) で分けられる。
// 南限 26.87: 母島の北端 26.721N (東京都) と与論島の南端 27.021N のあいだ。
//   与論島と小笠原の父島 (27.040-27.094N) は緯度が重なるので、経度と併せて初めて分けられる。
// 北限 30.1 は [MAIN_WINDOW] の南限と接しており、薩南諸島の連なりに隙間ができない。
private val SATSUNAN_WINDOW = GeoWindow(west = 120.0, south = 26.87, east = 130.7, north = 30.1)

// 本土側で描画する地理窓。どれかに収まるリングを描く。
// 除外しても 47 都道府県すべてが 1 つ以上のリングを残す。判定側 (PrefectureLocator) は
// 元のアセットをそのまま使うため、離島でのチェックインは従来どおり拾える。
private val MAIN_WINDOWS = listOf(MAIN_WINDOW, SATSUNAN_WINDOW)

// 沖縄インセットとして描画する地理窓。
// 東限 129.8: 沖縄本島の東端 128.338E と大東諸島の西端 131.212E のあいだ。
// 大東諸島は沖縄本島から約 350km 東に離れており、枠を横に引き伸ばすだけなので落とす。
private val INSET_WINDOWS = listOf(GeoWindow(west = 120.0, south = 20.0, east = 129.8, north = 40.0))

/**
 * 描画対象とする地理窓。境界は経度・緯度そのもので、外接矩形が完全に収まるリングだけを通す。
 */
private class GeoWindow(
  private val west: Double,
  private val south: Double,
  private val east: Double,
  private val north: Double,
) {
  /** [box] は [PrefectureBoundary.boundingBoxes] と同じ (west, south, east, north)。 */
  fun contains(box: DoubleArray): Boolean {
    return box[0] >= west && box[1] >= south && box[2] <= east && box[3] <= north
  }
}

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
 * 沖縄県は本土から遠いので、同じ縮尺のまま左上 (日本海側) のインセット枠に別途配置する。
 * 描画とヒットテストで同じ点列を使うため、判定と見た目がずれない。
 *
 * 小笠原諸島・南鳥島・大東諸島のような遠隔離島は列島を縮めてしまうため、
 * [MAIN_WINDOWS] / [INSET_WINDOWS] のどれにも収まらないリングを描画とスケール計算の両方から除く。
 * アセット自体は加工しないので、判定側はこれらの離島も従来どおり拾う。
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

  // 日本海にあたるキャンバス左上の空白に置く。本土は南西から北東へ斜めに伸びるため、
  // ここには本土のポリゴンが来ない (実測: 1080x1317 で最寄りの本土の点まで約 275px)
  val insetBounds: Rect = Rect(
    offset = Offset(insetMargin, insetMargin),
    size = Size(insetSide, insetSide),
  )

  /** 本土側と沖縄インセット側を連結したもの。インセットが後ろ = 最前面に描かれる。 */
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

    projected = project(main, mainArea, MAIN_WINDOWS) + project(inset, insetArea, INSET_WINDOWS)
  }

  // インセット枠は本土と重ならない位置に置いているため、走査順による取り違えは起きない
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
    windows: List<GeoWindow>,
  ): List<ProjectedPrefecture> {
    val visible = boundaries.mapNotNull { boundary ->
      val indices = boundary.boundingBoxes.indices.filter { index ->
        windows.any { it.contains(boundary.boundingBoxes[index]) }
      }
      if (indices.isEmpty()) {
        null
      } else {
        VisibleBoundary(
          prefecture = boundary.prefecture,
          rings = indices.map { boundary.rings[it] },
          boundingBoxes = indices.map { boundary.boundingBoxes[it] },
        )
      }
    }
    if (visible.isEmpty()) {
      return emptyList()
    }

    val bounds = computeProjectedBounds(visible)
    val sourceWidth = (bounds.maxX - bounds.minX).takeIf { it > 0f } ?: 1f
    val sourceHeight = (bounds.maxY - bounds.minY).takeIf { it > 0f } ?: 1f
    val scale = min(area.width / sourceWidth, area.height / sourceHeight)
    // アスペクト比を保ったまま領域の中央に置く
    val offsetX = area.left + (area.width - sourceWidth * scale) / 2
    val offsetY = area.top + (area.height - sourceHeight * scale) / 2

    return visible.map { boundary ->
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

  // リングごとの外接矩形 (west, south, east, north) が既にあるため、
  // 全点を走査せずそこから経度・緯度の外接範囲を求める。地理窓の外のリングは
  // ここに渡ってこないので、遠隔離島が縮尺を引き伸ばすことはない
  private fun computeProjectedBounds(boundaries: List<VisibleBoundary>): ProjectedBounds {
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
      // 辺が水平線 y をまたぐとき、その交点の x 座標を求めて左右を判定する
      val isAboveI = yi > y
      val isAboveJ = yj > y
      val intersectionX = (xj - xi) * (y - yi) / (yj - yi) + xi
      if (isAboveI != isAboveJ && x < intersectionX) {
        inside = !inside
      }
      j = i
    }
    return inside
  }

  /** 中間座標系（経度縮尺・緯度反転後）での外接矩形。 */
  private class ProjectedBounds(val minX: Float, val maxX: Float, val minY: Float, val maxY: Float)

  /** 地理窓を通過したリングだけを持つ 1 都道府県。 */
  private class VisibleBoundary(
    val prefecture: Prefecture,
    val rings: List<List<DoubleArray>>,
    val boundingBoxes: List<DoubleArray>,
  )
}
