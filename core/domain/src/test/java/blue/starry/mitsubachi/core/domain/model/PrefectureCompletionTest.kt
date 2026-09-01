package blue.starry.mitsubachi.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrefectureCompletionTest {
  @Test
  fun allFortySevenPrefecturesAreDefinedWithUniqueCodes() {
    assertEquals(47, Prefecture.entries.size)
    assertEquals((1..47).toList(), Prefecture.entries.map { it.code }.sorted())
  }

  @Test
  fun findsPrefectureByCode() {
    assertEquals(Prefecture.Hokkaido, Prefecture.fromCode(1))
    assertEquals(Prefecture.Tokyo, Prefecture.fromCode(13))
    assertEquals(Prefecture.Okinawa, Prefecture.fromCode(47))
    assertNull(Prefecture.fromCode(0))
    assertNull(Prefecture.fromCode(48))
  }

  @Test
  fun maxTotalScoreIs235() {
    assertEquals(235, PrefectureLevel.MaxTotalScore)
  }

  @Test
  fun scoreIsZeroWhenEveryPrefectureIsUnvisited() {
    val completions = Prefecture.entries.map { completion(it, PrefectureLevel.Unvisited) }

    assertEquals(0, completions.totalScore)
  }

  @Test
  fun scoreIsMaxWhenEveryPrefectureIsLived() {
    val completions = Prefecture.entries.map { completion(it, PrefectureLevel.Lived) }

    assertEquals(PrefectureLevel.MaxTotalScore, completions.totalScore)
  }

  @Test
  fun sumsScoresOfMixedLevels() {
    val completions = listOf(
      completion(Prefecture.Tokyo, PrefectureLevel.Lived),
      completion(Prefecture.Kanagawa, PrefectureLevel.Stayed),
      completion(Prefecture.Chiba, PrefectureLevel.Visited),
      completion(Prefecture.Saitama, PrefectureLevel.PassedThrough),
      completion(Prefecture.Gunma, PrefectureLevel.Unvisited),
    )

    assertEquals(5 + 4 + 3 + 1 + 0, completions.totalScore)
  }

  @Test
  fun manualLevelTakesPrecedenceOverAutomaticLevel() {
    val completion = PrefectureCompletion(
      prefecture = Prefecture.Tokyo,
      automaticLevel = PrefectureLevel.Visited,
      manualLevel = PrefectureLevel.Lived,
      venueCount = 10,
    )

    assertEquals(PrefectureLevel.Lived, completion.effectiveLevel)
  }

  @Test
  fun manualLevelTakesPrecedenceEvenWhenLowerThanAutomatic() {
    val completion = PrefectureCompletion(
      prefecture = Prefecture.Ibaraki,
      automaticLevel = PrefectureLevel.Visited,
      manualLevel = PrefectureLevel.PassedThrough,
      venueCount = 1,
    )

    assertEquals(PrefectureLevel.PassedThrough, completion.effectiveLevel)
    assertEquals(1, listOf(completion).totalScore)
  }

  @Test
  fun usesAutomaticLevelWhenManualLevelIsAbsent() {
    val completion = PrefectureCompletion(
      prefecture = Prefecture.Tokyo,
      automaticLevel = PrefectureLevel.Stayed,
      manualLevel = null,
      venueCount = 3,
    )

    assertEquals(PrefectureLevel.Stayed, completion.effectiveLevel)
  }

  private fun completion(prefecture: Prefecture, level: PrefectureLevel): PrefectureCompletion {
    return PrefectureCompletion(
      prefecture = prefecture,
      automaticLevel = level,
      manualLevel = null,
      venueCount = 0,
    )
  }
}
