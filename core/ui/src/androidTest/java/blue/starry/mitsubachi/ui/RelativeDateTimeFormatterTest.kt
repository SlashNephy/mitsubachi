package blue.starry.mitsubachi.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatterImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import android.icu.text.RelativeDateTimeFormatter as AndroidRelativeDateTimeFormatter

@RunWith(AndroidJUnit4::class)
class RelativeDateTimeFormatterTest {
  private lateinit var context: Context
  private lateinit var formatter: RelativeDateTimeFormatter

  @Before
  fun before() {
    context = ApplicationProvider.getApplicationContext()
    formatter = RelativeDateTimeFormatterImpl(
      context,
      AndroidRelativeDateTimeFormatter.getInstance(Locale.ENGLISH),
    )
  }

  @Test
  fun years() {
    assertEquals(
      "in 1 year",
      formatter.formatDuration(1.years, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 years",
      formatter.formatDuration(2.years, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 year ago",
      formatter.formatDuration(1.years, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 years ago",
      formatter.formatDuration(2.years, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun months() {
    assertEquals(
      "in 1 month",
      formatter.formatDuration(1.months, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 months",
      formatter.formatDuration(2.months, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 month ago",
      formatter.formatDuration(1.months, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 months ago",
      formatter.formatDuration(2.months, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun weeks() {
    assertEquals(
      "in 1 week",
      formatter.formatDuration(1.weeks, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 weeks",
      formatter.formatDuration(2.weeks, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 week ago",
      formatter.formatDuration(1.weeks, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 weeks ago",
      formatter.formatDuration(2.weeks, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun days() {
    assertEquals(
      "in 1 day",
      formatter.formatDuration(1.days, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 days",
      formatter.formatDuration(2.days, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 day ago",
      formatter.formatDuration(1.days, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 days ago",
      formatter.formatDuration(2.days, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun hours() {
    assertEquals(
      "in 1 hour",
      formatter.formatDuration(1.hours, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 hours",
      formatter.formatDuration(2.hours, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 hour ago",
      formatter.formatDuration(1.hours, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 hours ago",
      formatter.formatDuration(2.hours, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun minutes() {
    assertEquals(
      "in 1 minute",
      formatter.formatDuration(1.minutes, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 minutes",
      formatter.formatDuration(2.minutes, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 minute ago",
      formatter.formatDuration(1.minutes, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 minutes ago",
      formatter.formatDuration(2.minutes, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun seconds() {
    assertEquals(
      "in 1 second",
      formatter.formatDuration(1.seconds, RelativeDateTimeFormatter.Direction.NEXT),
    )
    assertEquals(
      "in 2 seconds",
      formatter.formatDuration(2.seconds, RelativeDateTimeFormatter.Direction.NEXT),
    )

    assertEquals(
      "1 second ago",
      formatter.formatDuration(1.seconds, RelativeDateTimeFormatter.Direction.LAST),
    )
    assertEquals(
      "2 seconds ago",
      formatter.formatDuration(2.seconds, RelativeDateTimeFormatter.Direction.LAST),
    )
  }

  @Test
  fun justNow() {
    assertEquals(
      context.getString(R.string.just_now),
      formatter.formatDuration(0.seconds, RelativeDateTimeFormatter.Direction.NEXT),
    )
  }

  @Test
  fun ignoreNegativeDuration() {
    assertEquals(
      "1 second ago",
      formatter.formatDuration((-1).seconds, RelativeDateTimeFormatter.Direction.LAST),
    )
  }
}

private val Int.years: Duration
  get() = 365.days * this

private val Int.months: Duration
  get() = 30.days * this

private val Int.weeks: Duration
  get() = 7.days * this
