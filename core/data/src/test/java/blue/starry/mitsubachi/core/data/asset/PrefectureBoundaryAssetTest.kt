package blue.starry.mitsubachi.core.data.asset

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLocator
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrefectureBoundaryAssetTest {
  private val boundaries by lazy {
    PrefectureBoundaryParser.parse(File("src/main/assets/prefectures.json").readText())
  }
  private val locator by lazy { PrefectureLocator(boundaries) }

  @Test
  fun assetContainsExactlyFortySevenPrefectures() {
    assertEquals(47, boundaries.size)
    assertEquals(Prefecture.entries.toSet(), boundaries.map { it.prefecture }.toSet())
  }

  @Test
  fun everyRingIsClosedAndHasAtLeastFourPoints() {
    for (boundary in boundaries) {
      assertTrue(boundary.rings.isNotEmpty(), "${boundary.prefecture} has no ring")
      for (ring in boundary.rings) {
        assertTrue(ring.size >= 4, "${boundary.prefecture} has a ring with ${ring.size} points")
        assertEquals(
          ring.first().toList(),
          ring.last().toList(),
          "${boundary.prefecture} has an unclosed ring",
        )
      }
    }
  }

  @Test
  fun noRingLiesInsideAnotherRingOfTheSamePrefecture() {
    // 内側リング (穴) があると、リングを順に見て最初に含んだものを返す判定
    // (PrefectureLocator.locateInside / JapanMapProjection.hitTest) が
    // 穴の内部の点を外側リングの都道府県と答えてしまう。
    // 現在のアセットには穴が 1 本もないため単純な走査で足りている。
    // データを更新して穴が入ったら、判定側に穴の除外を入れる必要がある
    for (boundary in boundaries) {
      for (inner in boundary.rings.indices) {
        for (outer in boundary.rings.indices) {
          if (inner == outer) {
            continue
          }
          val innerRing = boundary.rings[inner]
          val outerRing = boundary.rings[outer]
          // 穴なら内側リングの頂点はすべて外側リングの内部に入る
          assertTrue(
            innerRing.any { !contains(outerRing, x = it[0], y = it[1]) },
            "${boundary.prefecture} has ring #$inner inside ring #$outer",
          )
        }
      }
    }
  }

  @Test
  fun everyRepresentativeCityResolvesToItsOwnPrefecture() {
    for (fixture in cityFixtures) {
      assertEquals(
        fixture.prefecture,
        locator.locate(latitude = fixture.latitude, longitude = fixture.longitude),
        "${fixture.city} should be in ${fixture.prefecture}",
      )
    }
  }

  @Test
  fun resolvesNorthernTerritoriesToHokkaido() {
    // 元データ (admin-1) はロシア側に置いているため、係争地レイヤから補ったリングの確認
    assertEquals(Prefecture.Hokkaido, locator.locate(latitude = 45.0, longitude = 147.7)) // 択捉島
    assertEquals(Prefecture.Hokkaido, locator.locate(latitude = 44.0, longitude = 145.8)) // 国後島
    assertEquals(Prefecture.Hokkaido, locator.locate(latitude = 43.79, longitude = 146.75)) // 色丹島
  }

  @Test
  fun resolvesAmamiIslandsToKagoshima() {
    // 元データ (admin-1) は奄美群島を沖縄県側に置いているため、付け替えたリングの確認
    assertEquals(Prefecture.Kagoshima, locator.locate(latitude = 28.3775, longitude = 129.4936)) // 名瀬 (奄美大島)
    assertEquals(Prefecture.Kagoshima, locator.locate(latitude = 27.7222, longitude = 128.9861)) // 亀津 (徳之島)
    assertEquals(Prefecture.Kagoshima, locator.locate(latitude = 27.39, longitude = 128.60)) // 沖永良部島
    assertEquals(Prefecture.Kagoshima, locator.locate(latitude = 28.32, longitude = 129.98)) // 喜界島
    assertEquals(Prefecture.Kagoshima, locator.locate(latitude = 27.04, longitude = 128.43)) // 与論島
    // 与論島とは緯度が重なるが鹿児島県ではない、沖縄県最北の有人島
    assertEquals(Prefecture.Okinawa, locator.locate(latitude = 27.05, longitude = 127.97)) // 伊平屋島
    assertEquals(Prefecture.Okinawa, locator.locate(latitude = 25.94, longitude = 131.30)) // 北大東島
    assertEquals(Prefecture.Okinawa, locator.locate(latitude = 24.34, longitude = 124.16)) // 石垣島
  }

  @Test
  fun resolvesOffshorePointToNearestPrefecture() {
    // 東京湾上。20km 以内に陸地がある
    assertTrue(locator.locate(latitude = 35.45, longitude = 139.85) != null)
  }

  @Test
  fun doesNotResolvePointOutsideJapan() {
    assertNull(locator.locate(latitude = 37.5665, longitude = 126.9780)) // ソウル
    assertNull(locator.locate(latitude = 37.7749, longitude = -122.4194)) // サンフランシスコ
    assertNull(locator.locate(latitude = 25.0330, longitude = 121.5654)) // 台北
    assertNull(locator.locate(latitude = 14.5995, longitude = 120.9842)) // マニラ
  }

  private data class CityFixture(
    val prefecture: Prefecture,
    val city: String,
    val latitude: Double,
    val longitude: Double,
  )

  private companion object {
    /** 点 ([x], [y]) が閉リング [ring] の内部にあるか (交差数判定)。 */
    fun contains(ring: List<DoubleArray>, x: Double, y: Double): Boolean {
      var inside = false
      var j = ring.lastIndex
      for (i in ring.indices) {
        val xi = ring[i][0]
        val yi = ring[i][1]
        val xj = ring[j][0]
        val yj = ring[j][1]
        val isAboveI = yi > y
        val isAboveJ = yj > y
        if (isAboveI != isAboveJ && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
          inside = !inside
        }
        j = i
      }
      return inside
    }

    val cityFixtures = listOf(
      CityFixture(Prefecture.Hokkaido, "Sapporo", 43.0769, 141.3381),
      CityFixture(Prefecture.Aomori, "Aomori", 40.825, 140.71),
      CityFixture(Prefecture.Iwate, "Morioka", 39.72, 141.13),
      CityFixture(Prefecture.Miyagi, "Sendai", 38.2684, 140.8697),
      CityFixture(Prefecture.Akita, "Akita", 39.71, 140.09),
      CityFixture(Prefecture.Yamagata, "Yamagata", 38.2705, 140.32),
      CityFixture(Prefecture.Fukushima, "Iwaki", 37.0553, 140.89),
      CityFixture(Prefecture.Ibaraki, "Mito", 36.3704, 140.48),
      CityFixture(Prefecture.Tochigi, "Utsunomiya", 36.55, 139.87),
      CityFixture(Prefecture.Gunma, "Maebashi", 36.3927, 139.0727),
      CityFixture(Prefecture.Saitama, "Kawagoe", 35.9177, 139.4911),
      CityFixture(Prefecture.Chiba, "Chiba", 35.6074, 140.1065),
      CityFixture(Prefecture.Tokyo, "Tokyo", 35.687, 139.7495),
      CityFixture(Prefecture.Kanagawa, "Yokohama", 35.4307, 139.602),
      CityFixture(Prefecture.Niigata, "Niigata", 37.92, 139.04),
      CityFixture(Prefecture.Toyama, "Toyama", 36.7, 137.23),
      CityFixture(Prefecture.Ishikawa, "Kanazawa", 36.56, 136.64),
      CityFixture(Prefecture.Fukui, "Fukui", 36.0704, 136.22),
      CityFixture(Prefecture.Yamanashi, "Kofu", 35.6504, 138.5833),
      CityFixture(Prefecture.Nagano, "Nagano", 36.65, 138.17),
      CityFixture(Prefecture.Gifu, "Gifu", 35.4231, 136.7628),
      CityFixture(Prefecture.Shizuoka, "Hamamatsu", 34.7181, 137.7327),
      CityFixture(Prefecture.Aichi, "Nagoya", 35.1569, 136.913),
      CityFixture(Prefecture.Mie, "Tsu", 34.7171, 136.5167),
      CityFixture(Prefecture.Shiga, "Otsu", 35.0064, 135.8674),
      CityFixture(Prefecture.Kyoto, "Kyoto", 35.0319, 135.7481),
      CityFixture(Prefecture.Osaka, "Osaka", 34.6911, 135.5038),
      CityFixture(Prefecture.Hyogo, "Kobe", 34.68, 135.17),
      CityFixture(Prefecture.Nara, "Nara", 34.6851, 135.8048),
      CityFixture(Prefecture.Wakayama, "Wakayama", 34.2231, 135.1677),
      CityFixture(Prefecture.Tottori, "Tottori", 35.5004, 134.2333),
      CityFixture(Prefecture.Shimane, "Matsue", 35.467, 133.0666),
      CityFixture(Prefecture.Okayama, "Okayama", 34.672, 133.9171),
      CityFixture(Prefecture.Hiroshima, "Hiroshima", 34.3898, 132.441),
      CityFixture(Prefecture.Yamaguchi, "Shimonoseki", 33.9654, 130.9454),
      CityFixture(Prefecture.Tokushima, "Tokushima", 34.0674, 134.5525),
      CityFixture(Prefecture.Kagawa, "Takamatsu", 34.3447, 134.0448),
      CityFixture(Prefecture.Ehime, "Matsuyama", 33.8455, 132.7658),
      CityFixture(Prefecture.Kochi, "Kochi", 33.5624, 133.5375),
      CityFixture(Prefecture.Fukuoka, "Fukuoka", 33.597, 130.4081),
      CityFixture(Prefecture.Saga, "Saga", 33.2494, 130.2988),
      CityFixture(Prefecture.Nagasaki, "Nagasaki", 32.765, 129.885),
      CityFixture(Prefecture.Kumamoto, "Kumamoto", 32.8009, 130.7006),
      CityFixture(Prefecture.Oita, "Oita", 33.2432, 131.5979),
      CityFixture(Prefecture.Miyazaki, "Miyazaki", 31.9182, 131.4184),
      CityFixture(Prefecture.Kagoshima, "Kagoshima", 31.586, 130.5611),
      CityFixture(Prefecture.Okinawa, "Naha", 26.2072, 127.673),
    )
  }
}
