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
import kotlin.time.Duration

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

  override fun formatDuration(duration: Duration): String {
    val uLocale = ULocale.getDefault()
    val formatter = MeasureFormat.getInstance(uLocale, MeasureFormat.FormatWidth.SHORT)

    val seconds = duration.inWholeSeconds
    return when {
      seconds >= 3600 -> {
        val hours = duration.inWholeHours
        val measure = Measure(hours, MeasureUnit.HOUR)
        formatter.format(measure)
      }

      seconds >= 60 -> {
        val minutes = duration.inWholeMinutes
        val measure = Measure(minutes, MeasureUnit.MINUTE)
        formatter.format(measure)
      }

      else -> {
        val measure = Measure(seconds, MeasureUnit.SECOND)
        formatter.format(measure)
      }
    }
  }
}
