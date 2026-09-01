package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.ui.geometry.Offset
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 実アセットを投影したときの配置を固定するテスト。
 *
 * 沖縄インセットを日本海側（左上）に置けるのは日本列島が南西から北東へ斜めに伸びているからで、
 * 投影窓や枠の比率を変えると本土と重なりうる。合成データでは再現できないためアセットを直接読む。
 */
class JapanMapProjectionAssetTest {
  private val boundaries = parseAsset(File(ASSET_PATH).readText())

  @Test
  fun readsEveryPrefectureFromTheAsset() {
    assertEquals(47, boundaries.size)
    val points = boundaries.sumOf { boundary -> boundary.rings.sumOf { it.size } }
    assertTrue(points > 5000, "only $points points parsed")
    val longitudes = boundaries.flatMap { it.rings.flatten() }.map { it[0] }
    val latitudes = boundaries.flatMap { it.rings.flatten() }.map { it[1] }
    assertTrue(longitudes.min() > 122.0 && longitudes.max() < 154.0, "longitude out of Japan")
    assertTrue(latitudes.min() > 24.0 && latitudes.max() < 46.0, "latitude out of Japan")
  }

  @Test
  fun insetNeverOverlapsTheMainland() {
    for ((width, height) in CANVAS_SIZES) {
      val projection = JapanMapProjection(boundaries, width, height)
      for (item in projection.projected) {
        if (item.prefecture == Prefecture.Okinawa) {
          continue
        }
        for (ring in item.rings) {
          var index = 0
          while (index < ring.size) {
            val point = Offset(ring[index], ring[index + 1])
            assertTrue(
              !projection.insetBounds.contains(point),
              "${item.prefecture} overlaps the inset at $point on ${width}x$height",
            )
            index += 2
          }
        }
      }
    }
  }

  @Test
  fun drawsNorthernTerritoriesAsPartOfHokkaido() {
    val projection = JapanMapProjection(boundaries, width = 1080f, height = 1317f)
    val hokkaido = projection.projected.first { it.prefecture == Prefecture.Hokkaido }
    val boundary = boundaries.first { it.prefecture == Prefecture.Hokkaido }
    // 択捉島の東端 148.856E を含むリングが投影されていること
    val etorofu = boundary.boundingBoxes.count { it[2] > 148.0 }

    assertEquals(1, etorofu, "the asset should contain Etorofu as a Hokkaido ring")
    assertEquals(boundary.rings.size, hokkaido.rings.size, "a Hokkaido ring was dropped")
  }

  private companion object {
    // テストの作業ディレクトリは feature/map
    const val ASSET_PATH = "../../core/data/src/main/assets/prefectures.json"

    // 端末の縦横比 (MAP_ASPECT_RATIO = 0.82) と、他のテストが使う 400x600 を含める
    val CANVAS_SIZES = listOf(
      400f to 600f,
      400f to 488f,
      720f to 878f,
      1080f to 1317f,
    )

    val NUMBER = Regex("""-?\d+(?:\.\d+)?""")

    /**
     * アセットの JSON から都道府県ごとのリングを読む。
     *
     * 座標は `[[[経度, 緯度], ...], ...]` の入れ子配列で数値しか現れないため、
     * リング区切りで分割して数値を 2 つずつ取る。core:data のパーサは internal で
     * このモジュールからは見えない。
     */
    fun parseAsset(text: String): List<PrefectureBoundary> {
      return text.split("""{"code":""").drop(1).mapNotNull { chunk ->
        val code = chunk.takeWhile { it != ',' }.toInt()
        val rings = chunk.substringAfter("""rings":[[[""").substringBefore("]]]")
          .split("]],[[")
          .map { ring ->
            NUMBER.findAll(ring).map { it.value.toDouble() }.toList()
              .chunked(2) { doubleArrayOf(it[0], it[1]) }
          }
        Prefecture.fromCode(code)?.let { PrefectureBoundary(prefecture = it, rings = rings) }
      }
    }
  }
}
