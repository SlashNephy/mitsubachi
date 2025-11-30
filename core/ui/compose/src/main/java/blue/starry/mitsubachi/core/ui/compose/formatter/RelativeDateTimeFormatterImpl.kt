package blue.starry.mitsubachi.core.ui.compose.formatter

import android.content.Context
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import android.icu.util.ULocale
import android.text.format.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Singleton
class RelativeDateTimeFormatterImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : RelativeDateTimeFormatter {
  override fun formatAsRelativeDateTime(at: ZonedDateTime): String {
    return DateUtils
      .getRelativeTimeSpanString(
        context,
        at.toInstant().toEpochMilli(),
        false,
      )
      .toString()
  }

  override fun formatAsRelativeTimeSpan(at: ZonedDateTime): String {
    val now = Instant.now()
    return DateUtils
      .getRelativeTimeSpanString(
        at.toInstant().toEpochMilli(),
        now.toEpochMilli(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
      )
      .toString()
  }

  override fun formatDuration(duration: Duration, precision: Duration): String {
    val uLocale = ULocale.getDefault()
    val formatter = MeasureFormat.getInstance(uLocale, MeasureFormat.FormatWidth.SHORT)

    val measures = buildList {
      var remainingDuration = precision * ceil(duration / precision)

      val days = remainingDuration.inWholeDays
      if (days > 0) {
        add(Measure(days, MeasureUnit.DAY))
        remainingDuration -= days.days
      }

      val hours = remainingDuration.inWholeHours
      if (hours > 0) {
        add(Measure(hours, MeasureUnit.HOUR))
        remainingDuration -= hours.hours
      }

      val minutes = remainingDuration.inWholeMinutes
      if (minutes > 0) {
        add(Measure(minutes, MeasureUnit.MINUTE))
        remainingDuration -= minutes.minutes
      }

      val seconds = remainingDuration.inWholeSeconds
      if (seconds > 0) {
        add(Measure(seconds, MeasureUnit.SECOND))
      }
    }.toTypedArray()

    return formatter.formatMeasures(*measures)
  }
}
