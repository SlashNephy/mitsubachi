package blue.starry.mitsubachi.core.data.asset

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * `assets/prefectures.json` のスキーマ。
 * 生成手順は docs/superpowers/assets/prefectures/README.md を参照。
 */
@Serializable
internal data class PrefectureBoundaryAsset(
  val source: String,
  val simplifyTolerance: Double,
  val prefectures: List<Entry>,
) {
  @Serializable
  internal data class Entry(
    val code: Int,
    val name: String,
    // [経度, 緯度] の点からなる閉リングの配列
    val rings: List<List<List<Double>>>,
  )
}

internal fun PrefectureBoundaryAsset.toDomain(): List<PrefectureBoundary> {
  return prefectures.mapNotNull { entry ->
    Prefecture.fromCode(entry.code)?.let { prefecture ->
      PrefectureBoundary(
        prefecture = prefecture,
        rings = entry.rings.map { ring -> ring.map { doubleArrayOf(it[0], it[1]) } },
      )
    }
  }
}

internal object PrefectureBoundaryParser {
  private val json = Json { ignoreUnknownKeys = true }

  fun parse(text: String): List<PrefectureBoundary> {
    return json.decodeFromString<PrefectureBoundaryAsset>(text).toDomain()
  }
}
