package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrefectureLocatorTest {
  // 経度 139..140 / 緯度 35..36 の正方形を東京都に、経度 140..141 の隣接正方形を千葉県に見立てる
  private val locator = PrefectureLocator(
    listOf(
      PrefectureBoundary(Prefecture.Tokyo, listOf(square(139.0, 35.0, 140.0, 36.0))),
      PrefectureBoundary(Prefecture.Chiba, listOf(square(140.0, 35.0, 141.0, 36.0))),
    ),
  )

  @Test
  fun resolvesPointInsidePolygon() {
    assertEquals(Prefecture.Tokyo, locator.locate(latitude = 35.5, longitude = 139.5))
    assertEquals(Prefecture.Chiba, locator.locate(latitude = 35.5, longitude = 140.5))
  }

  @Test
  fun resolvesPointOutsidePolygonWithinTwentyKilometers() {
    // 経度 138.9 は東京ポリゴンの西側 0.1 度 (約 9km) の位置
    assertEquals(Prefecture.Tokyo, locator.locate(latitude = 35.5, longitude = 138.9))
  }

  @Test
  fun doesNotResolvePointFartherThanTwentyKilometers() {
    // 経度 138.0 は東京ポリゴンから約 90km 西
    assertNull(locator.locate(latitude = 35.5, longitude = 138.0))
    // 完全な国外
    assertNull(locator.locate(latitude = 37.5665, longitude = 126.9780))
    assertNull(locator.locate(latitude = 37.7749, longitude = -122.4194))
  }

  @Test
  fun resolvesAdjacentPolygonsWithoutOverlap() {
    assertEquals(Prefecture.Tokyo, locator.locate(latitude = 35.5, longitude = 139.99))
    assertEquals(Prefecture.Chiba, locator.locate(latitude = 35.5, longitude = 140.01))
  }

  @Test
  fun resolvesNothingWhenNoBoundaryIsGiven() {
    assertNull(PrefectureLocator(emptyList()).locate(latitude = 35.5, longitude = 139.5))
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
