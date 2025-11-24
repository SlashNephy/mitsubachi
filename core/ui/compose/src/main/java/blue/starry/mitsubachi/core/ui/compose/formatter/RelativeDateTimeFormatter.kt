package blue.starry.mitsubachi.core.ui.compose.formatter

import java.time.ZonedDateTime
import kotlin.time.Duration

interface RelativeDateTimeFormatter {
  fun formatAsRelativeDateTime(at: ZonedDateTime): String
  fun formatAsRelativeTimeSpan(at: ZonedDateTime): String
  fun formatDuration(duration: Duration, precision: Duration): String
}
