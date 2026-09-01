package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JapanMapProjectionTest {
  private val boundaries = listOf(
    // 本州側に見立てた 2 県。東京都には南鳥島に見立てた遠隔離島を持たせる
    PrefectureBoundary(
      Prefecture.Tokyo,
      listOf(square(139.0, 35.0, 140.0, 36.0), square(153.94, 24.29, 153.97, 24.32)),
    ),
    PrefectureBoundary(Prefecture.Hokkaido, listOf(square(141.0, 43.0, 143.0, 45.0))),
    // 遠く南西にある沖縄県。大東諸島に見立てた遠隔離島を持たせる
    PrefectureBoundary(
      Prefecture.Okinawa,
      listOf(square(127.0, 26.0, 128.0, 27.0), square(131.21, 25.82, 131.28, 25.88)),
    ),
  )
  private val projection = JapanMapProjection(boundaries, width = 400f, height = 600f)

  @Test
  fun projectsEveryPrefecture() {
    assertEquals(3, projection.projected.size)
    assertEquals(
      boundaries.map { it.prefecture }.toSet(),
      projection.projected.map { it.prefecture }.toSet(),
    )
  }

  @Test
  fun projectedCoordinatesFitInsideCanvas() {
    for (projected in projection.projected) {
      for (ring in projected.rings) {
        var index = 0
        while (index < ring.size) {
          assertTrue(ring[index] in 0f..400f, "x=${ring[index]} out of canvas")
          assertTrue(ring[index + 1] in 0f..600f, "y=${ring[index + 1]} out of canvas")
          index += 2
        }
      }
    }
  }

  @Test
  fun drawsNorthernPrefecturesHigher() {
    val tokyo = projection.centerOf(Prefecture.Tokyo)
    val hokkaido = projection.centerOf(Prefecture.Hokkaido)

    assertTrue(hokkaido.second < tokyo.second, "Hokkaido should be drawn above Tokyo")
  }

  @Test
  fun drawsOkinawaInBottomLeftInset() {
    val okinawa = projection.centerOf(Prefecture.Okinawa)

    assertTrue(okinawa.first < 400f * 0.3f, "Okinawa should be near the left edge")
    assertTrue(okinawa.second > 600f * 0.7f, "Okinawa should be near the bottom edge")
  }

  @Test
  fun hitTestReturnsPrefectureAtItsCenter() {
    for (prefecture in listOf(Prefecture.Tokyo, Prefecture.Hokkaido, Prefecture.Okinawa)) {
      val (x, y) = projection.centerOf(prefecture)
      assertEquals(prefecture, projection.hitTest(x, y), "hit test failed for $prefecture")
    }
  }

  @Test
  fun hitTestReturnsNullOutsideEveryPrefecture() {
    assertNull(projection.hitTest(-10f, -10f))
    assertNull(projection.hitTest(399f, 1f))
  }

  @Test
  fun dropsRemoteIslandRings() {
    // 南鳥島・大東諸島に見立てたリングは描画対象から外れ、本島側のリングだけが残る
    assertEquals(1, projection.ringsOf(Prefecture.Tokyo).size)
    assertEquals(1, projection.ringsOf(Prefecture.Okinawa).size)
  }

  @Test
  fun mainlandFillsMostOfCanvas() {
    // 北海道は 2 度四方。遠隔離島まで縮尺に含めると本土は半分以下に縮むので、
    // 400x600 のキャンバスでの実測値をしきい値に直に書く
    val hokkaido = projection.boundsOf(Prefecture.Hokkaido)
    assertTrue(hokkaido.height >= 100f, "Hokkaido height ${hokkaido.height} is too small")
    assertTrue(hokkaido.width >= 80f, "Hokkaido width ${hokkaido.width} is too small")

    // 本土全体が縦方向にキャンバスをほぼ使い切る
    val mainland = projection.boundsOf(Prefecture.Tokyo, Prefecture.Hokkaido)
    assertTrue(mainland.height >= 540f, "mainland height ${mainland.height} is too small")
  }

  @Test
  fun okinawaFillsMostOfInset() {
    val bounds = projection.boundsOf(Prefecture.Okinawa)

    for (ring in projection.ringsOf(Prefecture.Okinawa)) {
      var index = 0
      while (index < ring.size) {
        assertTrue(
          projection.insetBounds.contains(Offset(ring[index], ring[index + 1])),
          "Okinawa point (${ring[index]}, ${ring[index + 1]}) is outside the inset",
        )
        index += 2
      }
    }
    // インセット枠の一辺は 400x600 のキャンバスで 104px。大東諸島が混じるとその 2 割程度に縮む
    assertTrue(bounds.width >= 50f, "Okinawa width ${bounds.width} is too small")
    assertTrue(bounds.height >= 50f, "Okinawa height ${bounds.height} is too small")
  }

  @Test
  fun hitTestPrefersOkinawaInsideInset() {
    // 本土がインセット枠を覆う極端な形。枠内では最前面の沖縄が勝ち、
    // 沖縄のポリゴンから外れた点は枠の下に見えている本土を拾う
    val overlapping = JapanMapProjection(
      listOf(
        PrefectureBoundary(Prefecture.Tokyo, listOf(square(128.6, 30.3, 145.8, 45.5))),
        PrefectureBoundary(Prefecture.Okinawa, listOf(square(127.0, 26.0, 128.0, 27.0))),
      ),
      width = 400f,
      height = 600f,
    )
    val okinawa = overlapping.boundsOf(Prefecture.Okinawa)
    val inset = overlapping.insetBounds

    assertEquals(
      Prefecture.Okinawa,
      overlapping.hitTest(okinawa.center.x, okinawa.center.y),
      "Okinawa should win where both polygons contain the point",
    )
    // 沖縄は枠の中央に置かれるので、枠の左上の角付近は沖縄の外側
    assertEquals(
      Prefecture.Tokyo,
      overlapping.hitTest(inset.left + 1f, inset.top + 1f),
      "the mainland visible under the inset frame should stay tappable",
    )
  }

  private fun JapanMapProjection.ringsOf(prefecture: Prefecture): List<FloatArray> {
    return projected.first { it.prefecture == prefecture }.rings
  }

  private fun JapanMapProjection.boundsOf(vararg prefectures: Prefecture): Rect {
    val rings = prefectures.flatMap { ringsOf(it) }
    val xs = rings.flatMap { ring -> ring.filterIndexed { index, _ -> index % 2 == 0 } }
    val ys = rings.flatMap { ring -> ring.filterIndexed { index, _ -> index % 2 == 1 } }
    return Rect(xs.min(), ys.min(), xs.max(), ys.max())
  }

  private fun JapanMapProjection.centerOf(prefecture: Prefecture): Pair<Float, Float> {
    val ring = projected.first { it.prefecture == prefecture }.rings.first()
    var sumX = 0f
    var sumY = 0f
    var count = 0
    var index = 0
    // 閉リングの終点は始点と同じなので最後の 1 点を除いて平均する
    while (index < ring.size - 2) {
      sumX += ring[index]
      sumY += ring[index + 1]
      count++
      index += 2
    }
    return sumX / count to sumY / count
  }

  private fun square(
    west: Double,
    south: Double,
    east: Double,
    north: Double,
  ): List<DoubleArray> {
    return listOf(
      doubleArrayOf(west, south),
      doubleArrayOf(east, south),
      doubleArrayOf(east, north),
      doubleArrayOf(west, north),
      doubleArrayOf(west, south),
    )
  }
}
