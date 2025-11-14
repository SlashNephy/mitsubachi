package blue.starry.mitsubachi.core.ui.compose.formatter

import java.time.ZonedDateTime
import kotlin.time.Duration
import java.time.Duration as JavaDuration

interface RelativeDateTimeFormatter {
  enum class Direction {
    LAST,
    NEXT,
  }

  fun formatDuration(duration: JavaDuration, direction: Direction): String
  fun formatDuration(duration: Duration, direction: Direction): String
  fun formatPastDateTime(pastDateTime: ZonedDateTime): String
}
