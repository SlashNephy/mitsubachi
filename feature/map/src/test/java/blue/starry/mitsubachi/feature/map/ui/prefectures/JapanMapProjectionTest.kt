package blue.starry.mitsubachi.feature.map.ui.prefectures

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JapanMapProjectionTest {
  private val boundaries = listOf(
    // 本州側に見立てた 2 県
    PrefectureBoundary(Prefecture.Tokyo, listOf(square(139.0, 35.0, 140.0, 36.0))),
    PrefectureBoundary(Prefecture.Hokkaido, listOf(square(141.0, 43.0, 143.0, 45.0))),
    // 遠く南西にある沖縄県
    PrefectureBoundary(Prefecture.Okinawa, listOf(square(127.0, 26.0, 128.0, 27.0))),
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
  fun exposesInsetBounds() {
    assertNotNull(projection.insetBounds)
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
