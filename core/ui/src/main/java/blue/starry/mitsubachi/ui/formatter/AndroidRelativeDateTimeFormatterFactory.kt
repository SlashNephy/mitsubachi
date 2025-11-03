package blue.starry.mitsubachi.ui.formatter

import android.icu.text.RelativeDateTimeFormatter as AndroidRelativeDateTimeFormatter

fun interface AndroidRelativeDateTimeFormatterFactory {
  fun create(): AndroidRelativeDateTimeFormatter
}
