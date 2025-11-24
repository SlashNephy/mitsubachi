package blue.starry.mitsubachi.core.ui.compose.formatter

import android.content.Context
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import android.icu.util.ULocale
import android.text.format.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val now = System.currentTimeMillis()
    return DateUtils
      .getRelativeTimeSpanString(
        at.toInstant().toEpochMilli(),
        now,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
      )
      .toString()
  }

  override fun formatDuration(duration: Duration, precision: Duration): String {
    val uLocale = ULocale.getDefault()
    val formatter = MeasureFormat.getInstance(uLocale, MeasureFormat.FormatWidth.SHORT)

    val measures = buildList {
      val factor = ceil(duration / precision)
      var duration = precision * factor

      val days = duration.inWholeDays
      if (days > 0) {
        add(Measure(days, MeasureUnit.DAY))
        duration -= days.days
      }

      val hours = duration.inWholeHours
      if (hours > 0) {
        add(Measure(hours, MeasureUnit.HOUR))
        duration -= hours.hours
      }

      val minutes = duration.inWholeMinutes
      if (minutes > 0) {
        add(Measure(minutes, MeasureUnit.MINUTE))
        duration -= minutes.minutes
      }

      val seconds = duration.inWholeSeconds - 60 * minutes
      if (seconds > 0) {
        add(Measure(seconds, MeasureUnit.SECOND))
      }
    }

    return formatter.formatMeasures(*measures.toTypedArray())
  }
}
