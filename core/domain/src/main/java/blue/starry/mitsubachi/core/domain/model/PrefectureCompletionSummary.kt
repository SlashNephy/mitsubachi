package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

/**
 * 県踏破度の集計結果。
 *
 * @property completions 47 都道府県分。コード順に並ぶ
 * @property visitedCountryCodes 日本以外でチェックインした国の ISO 3166-1 alpha-2 コード
 */
@Immutable
data class PrefectureCompletionSummary(
  val completions: ImmutableList<PrefectureCompletion>,
  val visitedCountryCodes: ImmutableSet<String>,
) {
  val totalScore: Int
    get() = completions.totalScore

  val maxScore: Int
    get() = PrefectureLevel.MaxTotalScore
}
