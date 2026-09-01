package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 1 都道府県分の境界ポリゴン。
 *
 * [DoubleArray] は型としては可変だが、[Immutable] の契約は「構築後に配列を書き換えない」ことで成り立つ。
 * この型を作るのは core:data の `PrefectureBoundaryParser` だけで、アセットの JSON から一度だけ組み立てる。
 * その結果は `PrefectureBoundaryRepositoryImpl` がキャッシュして使い回し、以降は読み取りしか行わない。
 * 座標を書き換える経路はどこにもない。
 *
 * 座標を不変の値型に置き換えないのは、6000 点規模のオブジェクト割り当てが増え、
 * ベニューごとに呼ばれる内外判定のホットパスを重くするため。
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
