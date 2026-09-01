package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 1 都道府県分の踏破状況。
 *
 * @property prefecture 対象の都道府県
 * @property automaticLevel チェックイン履歴から自動判定したレベル
 * @property manualLevel ユーザーが手動で設定したレベル。未設定なら null
 * @property venueCount 自動判定の根拠になったベニュー数
 */
@Immutable
data class PrefectureCompletion(
  val prefecture: Prefecture,
  val automaticLevel: PrefectureLevel,
  val manualLevel: PrefectureLevel?,
  val venueCount: Int,
) {
  /** 手動上書きがあればそれを、なければ自動判定を採用する。上書きが自動判定より低くても上書きを優先する。 */
  val effectiveLevel: PrefectureLevel
    get() = manualLevel ?: automaticLevel
}

val List<PrefectureCompletion>.totalScore: Int
  get() = sumOf { it.effectiveLevel.score }
