package blue.starry.mitsubachi.core.ui.compose.formatter

import android.content.Context
import android.text.format.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

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
}
