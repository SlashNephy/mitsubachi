package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import java.text.Normalizer

/**
 * Foursquare の `VenueLocation.state` を都道府県に解決する。
 *
 * 実データの state は英語表記 (Tokyo Prefecture)、マクロンつきローマ字 (Hokkaidō)、日本語表記 (沖縄県)、
 * 市町村名の混入 (沖縄県伊良部町)、複数県のスラッシュ区切り (東京都/北海道) が混在している。
 * 解決できない値は null を返し、呼び出し側で座標判定にフォールバックする。
 */
object PrefectureNameResolver {
  private val byRomaji = Prefecture.entries.associateBy { it.romajiName }

  fun resolve(state: String?): Prefecture? {
    val trimmed = state?.trim().orEmpty()
    if (trimmed.isEmpty()) {
      return null
    }

    // 複数県を跨ぐ値はどちらか一方に寄せられないため、座標判定に委ねる
    if (trimmed.contains('/')) {
      return null
    }

    // 日本語表記は市町村名が続くことがあるので前方一致で判定する
    Prefecture.entries.firstOrNull { trimmed.startsWith(it.japaneseName) }?.also {
      return it
    }

    return byRomaji[normalizeRomaji(trimmed)]
  }

  private fun normalizeRomaji(value: String): String {
    return Normalizer.normalize(value, Normalizer.Form.NFKD)
      .filter { !it.isMarkCharacter() }
      .lowercase()
      .replace("prefecture", "")
      .replace("-ken", "")
      .filter { !it.isWhitespace() && it != '\'' && it != '’' }
  }

  private fun Char.isMarkCharacter(): Boolean {
    val type = Character.getType(this).toByte()
    return type == Character.NON_SPACING_MARK ||
      type == Character.COMBINING_SPACING_MARK ||
      type == Character.ENCLOSING_MARK
  }
}
