package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.math.cos
import kotlin.math.sqrt

/** ポリゴン外の点を最寄りの都道府県に寄せる上限距離 (km)。 */
const val FALLBACK_DISTANCE_KILOMETERS = 20.0

private const val KILOMETERS_PER_DEGREE = 111.0

/**
 * 座標から都道府県を引く。
 *
 * ポリゴンは簡略化されているため海岸線付近の点が外れることがある。
 * どのポリゴンにも入らない点は最寄りのポリゴンの辺までの距離を測り、[FALLBACK_DISTANCE_KILOMETERS] 以内なら
 * その都道府県に寄せる。それを超えたら null を返す。
 */
class PrefectureLocator(private val boundaries: List<PrefectureBoundary>) {
  fun locate(latitude: Double, longitude: Double): Prefecture? {
    return locateInside(latitude, longitude) ?: locateNearest(latitude, longitude)
  }

  private fun locateInside(latitude: Double, longitude: Double): Prefecture? {
    for (boundary in boundaries) {
      for (index in boundary.rings.indices) {
        val box = boundary.boundingBoxes[index]
        if (isOutsideBoundingBox(box, latitude, longitude)) {
          continue
        }
        if (contains(boundary.rings[index], longitude, latitude)) {
          return boundary.prefecture
        }
      }
    }
    return null
  }

  private fun isOutsideBoundingBox(box: DoubleArray, latitude: Double, longitude: Double): Boolean {
    val isOutsideLongitude = longitude < box[0] || longitude > box[2]
    val isOutsideLatitude = latitude < box[1] || latitude > box[3]
    return isOutsideLongitude || isOutsideLatitude
  }

  private fun locateNearest(latitude: Double, longitude: Double): Prefecture? {
    // 緯度による経度の縮尺を補正して比較する
    val longitudeScale = cos(Math.toRadians(latitude))
    val pointX = longitude * longitudeScale
    val pointY = latitude
    var nearest: Prefecture? = null
    var nearestSquaredDegrees = Double.MAX_VALUE

    for (boundary in boundaries) {
      for (ring in boundary.rings) {
        for (index in 0 until ring.lastIndex) {
          val ax = ring[index][0] * longitudeScale
          val ay = ring[index][1]
          val bx = ring[index + 1][0] * longitudeScale
          val by = ring[index + 1][1]
          val squared = squaredDistanceToSegment(pointX, pointY, ax, ay, bx, by)
          if (squared < nearestSquaredDegrees) {
            nearestSquaredDegrees = squared
            nearest = boundary.prefecture
          }
        }
      }
    }

    if (nearest == null) {
      return null
    }
    val kilometers = sqrt(nearestSquaredDegrees) * KILOMETERS_PER_DEGREE
    return nearest.takeIf { kilometers <= FALLBACK_DISTANCE_KILOMETERS }
  }

  // 点 (px, py) から線分 (ax, ay)-(bx, by) までの最短距離の二乗を求める
  private fun squaredDistanceToSegment(
    px: Double,
    py: Double,
    ax: Double,
    ay: Double,
    bx: Double,
    by: Double,
  ): Double {
    val segmentDx = bx - ax
    val segmentDy = by - ay
    val segmentLengthSquared = segmentDx * segmentDx + segmentDy * segmentDy
    val t = if (segmentLengthSquared == 0.0) {
      0.0
    } else {
      (((px - ax) * segmentDx + (py - ay) * segmentDy) / segmentLengthSquared).coerceIn(0.0, 1.0)
    }
    val closestX = ax + t * segmentDx
    val closestY = ay + t * segmentDy
    val dx = px - closestX
    val dy = py - closestY
    return dx * dx + dy * dy
  }

  private fun contains(ring: List<DoubleArray>, x: Double, y: Double): Boolean {
    var inside = false
    var j = ring.lastIndex
    for (i in ring.indices) {
      val xi = ring[i][0]
      val yi = ring[i][1]
      val xj = ring[j][0]
      val yj = ring[j][1]
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
}
