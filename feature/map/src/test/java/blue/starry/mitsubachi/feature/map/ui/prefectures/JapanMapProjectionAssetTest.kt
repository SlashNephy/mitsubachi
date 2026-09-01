package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
          // 頂点だけでなく辺も見る。枠より長い辺が頂点を枠の外に持ったまま横切ることがある
          var index = 0
          while (index < ring.size) {
            val from = Offset(ring[index], ring[index + 1])
            val next = (index + 2) % ring.size
            val to = Offset(ring[next], ring[next + 1])
            assertTrue(
              !intersects(projection.insetBounds, from, to),
              "${item.prefecture} overlaps the inset at $from-$to on ${width}x$height",
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
    // 択捉島・国後島・色丹島・歯舞群島がそれぞれリングとして存在すること
    val etorofu = boundary.boundingBoxes.count { it[2] > 148.0 }
    val kunashiri = boundary.boundingBoxes.count { it[0] > 145.4 && it[3] > 44.4 && it[2] < 146.6 }
    val shikotan = boundary.boundingBoxes.count { it[0] > 146.6 && it[3] in 43.7..44.0 }
    val habomai = boundary.boundingBoxes.count { it[0] > 145.8 && it[3] < 43.7 }

    assertEquals(1, etorofu, "the asset should contain Etorofu as a Hokkaido ring")
    assertEquals(1, kunashiri, "the asset should contain Kunashiri as a Hokkaido ring")
    assertEquals(1, shikotan, "the asset should contain Shikotan as a Hokkaido ring")
    assertEquals(4, habomai, "the asset should contain the Habomai islands as Hokkaido rings")
    // 本土の投影窓 (東限 151.4E) はこれらを 1 本も落とさない
    assertEquals(boundary.rings.size, hokkaido.rings.size, "a Hokkaido ring was dropped")
  }

  @Test
  fun drawsAmamiIslandsWithKagoshimaSouthOfKyushu() {
    val projection = JapanMapProjection(boundaries, width = 1080f, height = 1317f)
    val kagoshima = projection.projected.first { it.prefecture == Prefecture.Kagoshima }
    val boundary = boundaries.first { it.prefecture == Prefecture.Kagoshima }
    // 元データ (admin-1) は奄美群島を沖縄県側に置いているため、付け替えたリングの確認。
    // 与論島 27.021N 〜 奄美大島 28.510N / 与論島 128.396E 〜 喜界島 130.030E に収まる 6 本
    val amami = boundary.boundingBoxes.indices.filter {
      val box = boundary.boundingBoxes[it]
      box[1] > 26.87 && box[3] < 29.0 && box[2] < 130.7
    }
    assertEquals(6, amami.size, "the asset should assign the Amami islands to Kagoshima")
    // 薩南諸島の投影窓がトカラ列島も奄美群島も落とさないこと。
    // 1 本も落ちないのでリングの並びは投影の前後で対応する
    assertEquals(boundary.rings.size, kagoshima.rings.size, "a Kagoshima ring was dropped")

    // 九州本土 (最も点数の多いリング) より南、つまり画面では下に描かれること
    val kyushu = kagoshima.rings.maxBy { ring -> ring.size }
    val kyushuBottom = (1 until kyushu.size step 2).maxOf { kyushu[it] }
    for (index in amami) {
      val ring = kagoshima.rings[index]
      val top = (1 until ring.size step 2).minOf { ring[it] }
      assertTrue(top > kyushuBottom, "an Amami ring is drawn north of Kyushu at $top")
    }
  }

  private companion object {
    // テストの作業ディレクトリは feature/map
    const val ASSET_PATH = "../../core/data/src/main/assets/prefectures.json"

    /** 線分 [from]-[to] が矩形 [rect] と交わるか。端点が内側にある場合も含む。 */
    fun intersects(rect: Rect, from: Offset, to: Offset): Boolean {
      if (rect.contains(from) || rect.contains(to)) {
        return true
      }
      val corners = listOf(
        Offset(rect.left, rect.top) to Offset(rect.right, rect.top),
        Offset(rect.right, rect.top) to Offset(rect.right, rect.bottom),
        Offset(rect.right, rect.bottom) to Offset(rect.left, rect.bottom),
        Offset(rect.left, rect.bottom) to Offset(rect.left, rect.top),
      )
      return corners.any { (a, b) -> crosses(from, to, a, b) }
    }

    /** 線分同士が交差するか。向きの符号が両側で入れ替わるかで判定する。 */
    fun crosses(p1: Offset, p2: Offset, p3: Offset, p4: Offset): Boolean {
      val d1 = direction(p3, p4, p1)
      val d2 = direction(p3, p4, p2)
      val d3 = direction(p1, p2, p3)
      val d4 = direction(p1, p2, p4)
      return d1 > 0 != d2 > 0 && d3 > 0 != d4 > 0
    }

    fun direction(a: Offset, b: Offset, c: Offset): Float {
      return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    }

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
