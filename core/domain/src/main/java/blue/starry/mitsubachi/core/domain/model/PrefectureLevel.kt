package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 都道府県との関わりの深さ。[score] がそのまま得点になる。
 */
@Immutable
enum class PrefectureLevel(val score: Int) {
  Unvisited(0),
  PassedThrough(1),
  Landed(2),
  Visited(3),
  Stayed(4),
  Lived(5),
  ;

    companion object {
    /** 47 都道府県すべてが [Lived] のときの得点。 */
    val MaxTotalScore: Int = Prefecture.entries.size * Lived.score

    private val byScore = entries.associateBy { it.score }

    fun fromScore(score: Int): PrefectureLevel? {
      return byScore[score]
    }
  }
}
