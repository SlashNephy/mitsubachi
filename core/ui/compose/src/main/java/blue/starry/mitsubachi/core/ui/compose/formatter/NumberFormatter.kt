package blue.starry.mitsubachi.core.ui.compose.formatter

import android.icu.number.LocalizedNumberFormatter
import android.icu.number.Notation
import android.icu.util.ULocale
import android.icu.number.NumberFormatter as AndroidNumberFormatter

object NumberFormatter {
  private val formatter: LocalizedNumberFormatter
    get() {
      val locale = ULocale.getDefault()
      return AndroidNumberFormatter.withLocale(locale)
    }

  fun formatInt(value: Int): String {
    return formatter.format(value).toString()
  }

  fun formatIntWithShortNotation(value: Int): String {
    return formatter.notation(Notation.compactShort()).format(value).toString()
  }
}
