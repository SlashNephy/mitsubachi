package blue.starry.mitsubachi.core.domain.model

/**
 * チェックインの 1 ページ分の取得結果。
 *
 * @property checkIns 新しい順に並んだチェックイン
 * @property nextMarker より古いページを取得するためのマーカー。null の場合は末尾に到達している
 */
data class CheckInPage(
  val checkIns: List<CheckIn>,
  val nextMarker: String?,
)
