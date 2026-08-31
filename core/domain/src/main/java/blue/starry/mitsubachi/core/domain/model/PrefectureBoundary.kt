package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 1 都道府県分の境界ポリゴン。
 *
 * @property prefecture 対象の都道府県
 * @property rings 閉リングの配列。各点は `doubleArrayOf(経度, 緯度)`。始点と終点は一致する
 */
@Immutable
data class PrefectureBoundary(
  val prefecture: Prefecture,
  val rings: List<List<DoubleArray>>,
) {
  /** リングごとの外接矩形 (west, south, east, north)。判定の枝刈りに使う。 */
  val boundingBoxes: List<DoubleArray> = rings.map { ring ->
    doubleArrayOf(
      ring.minOf { it[0] },
      ring.minOf { it[1] },
      ring.maxOf { it[0] },
      ring.maxOf { it[1] },
    )
  }
}
