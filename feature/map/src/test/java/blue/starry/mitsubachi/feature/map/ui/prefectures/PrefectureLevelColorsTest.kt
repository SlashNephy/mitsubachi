package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.luminance
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrefectureLevelColorsTest {
  private val light = lightColorScheme()
  private val dark = darkColorScheme()

  @Test
  fun unvisitedUsesSurfaceColorItself() {
    assertEquals(light.surfaceContainerLow, light.prefectureLevelColor(PrefectureLevel.Unvisited))
    assertEquals(dark.surfaceContainerLow, dark.prefectureLevelColor(PrefectureLevel.Unvisited))
  }

  @Test
  fun getsDarkerAsLevelRisesInLightTheme() {
    val luminances = PrefectureLevel.entries.map { light.prefectureLevelColor(it).luminance() }

    for (index in 1 until luminances.size) {
      assertTrue(
        luminances[index] < luminances[index - 1],
        "level $index should be darker than level ${index - 1}",
      )
    }
  }

  @Test
  fun getsBrighterAsLevelRisesInDarkTheme() {
    val luminances = PrefectureLevel.entries.map { dark.prefectureLevelColor(it).luminance() }

    for (index in 1 until luminances.size) {
      assertTrue(
        luminances[index] > luminances[index - 1],
        "level $index should be brighter than level ${index - 1}",
      )
    }
  }

  @Test
  fun keepsLuminanceGapBetweenAdjacentLevels() {
    for (scheme in listOf(light, dark)) {
      val luminances = PrefectureLevel.entries.map { scheme.prefectureLevelColor(it).luminance() }
      for (index in 1 until luminances.size) {
        val difference = kotlin.math.abs(luminances[index] - luminances[index - 1])
        // 本番コードの定数を参照すると、その値が下げられた場合に回帰が検知できなくなるため、ハードコードする
        assertTrue(
          difference >= 0.02f,
          "levels ${index - 1} and $index differ by only $difference",
        )
      }
    }
  }

  @Test
  fun allSixLevelsGetDistinctColors() {
    for (scheme in listOf(light, dark)) {
      val colors = PrefectureLevel.entries.map { scheme.prefectureLevelColor(it) }
      assertEquals(colors.size, colors.toSet().size)
    }
  }
}
