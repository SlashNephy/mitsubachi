package blue.starry.mitsubachi.core.ui.compose.formatter

import java.time.ZonedDateTime

interface RelativeDateTimeFormatter {
  fun formatAsRelativeDateTime(at: ZonedDateTime): String
  fun formatAsRelativeTimeSpan(at: ZonedDateTime): String
}
