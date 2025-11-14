package blue.starry.mitsubachi.core.ui.compose.formatter

import android.icu.text.RelativeDateTimeFormatter as AndroidRelativeDateTimeFormatter

fun interface AndroidRelativeDateTimeFormatterFactory {
  fun create(): AndroidRelativeDateTimeFormatter
}
