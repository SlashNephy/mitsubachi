package blue.starry.mitsubachi.ui.formatter

import android.content.Context
import blue.starry.mitsubachi.domain.ApplicationScope
import blue.starry.mitsubachi.domain.usecase.DeviceLocaleRepository
import blue.starry.mitsubachi.ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import android.icu.text.RelativeDateTimeFormatter as AndroidRelativeDateTimeFormatter
import java.time.Duration as JavaDuration

@Singleton
class RelativeDateTimeFormatterImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
  factory: AndroidRelativeDateTimeFormatterFactory,
  @ApplicationScope applicationScope: CoroutineScope,
  deviceLocaleRepository: DeviceLocaleRepository,
) : RelativeDateTimeFormatter {
  @Volatile
  private var underlyingFormatter = factory.create()

  init {
    applicationScope.launch {
      deviceLocaleRepository.onLocaleChanged.collect {
        underlyingFormatter = factory.create()
      }
    }
  }

  override fun formatDuration(
    duration: JavaDuration,
    direction: RelativeDateTimeFormatter.Direction,
  ): String {
    return formatDuration(duration.toKotlinDuration(), direction)
  }

  override fun formatDuration(
    duration: Duration,
    direction: RelativeDateTimeFormatter.Direction,
  ): String {
    for ((unit, converter) in unitConverters) {
      val value = converter(duration).absoluteValue
      if (value > 0) {
        return underlyingFormatter.format(
          value.toDouble(),
          directionMap[direction],
          unit,
        )
      }
    }

    return context.getString(R.string.just_now)
  }

  override fun formatPastDateTime(pastDateTime: ZonedDateTime): String {
    val now = ZonedDateTime.now()
    if (now < pastDateTime) {
      return context.getString(R.string.just_now)
    }

    return formatDuration(
      JavaDuration.between(pastDateTime, now),
      RelativeDateTimeFormatter.Direction.LAST,
    )
  }
}

private val directionMap = mapOf(
  RelativeDateTimeFormatter.Direction.LAST to AndroidRelativeDateTimeFormatter.Direction.LAST,
  RelativeDateTimeFormatter.Direction.NEXT to AndroidRelativeDateTimeFormatter.Direction.NEXT,
)

private val unitConverters = listOf(
  AndroidRelativeDateTimeFormatter.RelativeUnit.YEARS to Duration::inWholeYears,
  AndroidRelativeDateTimeFormatter.RelativeUnit.MONTHS to Duration::inWholeMonths,
  AndroidRelativeDateTimeFormatter.RelativeUnit.WEEKS to Duration::inWholeWeeks,
  AndroidRelativeDateTimeFormatter.RelativeUnit.DAYS to Duration::inWholeDays,
  AndroidRelativeDateTimeFormatter.RelativeUnit.HOURS to Duration::inWholeHours,
  AndroidRelativeDateTimeFormatter.RelativeUnit.MINUTES to Duration::inWholeMinutes,
  AndroidRelativeDateTimeFormatter.RelativeUnit.SECONDS to Duration::inWholeSeconds,
)

private const val YEAR_IN_DAYS = 365
private val Duration.inWholeYears: Long
  get() = this.inWholeDays / YEAR_IN_DAYS

private const val MONTH_IN_DAYS = 30
private val Duration.inWholeMonths: Long
  get() = this.inWholeDays / MONTH_IN_DAYS

private const val WEEK_IN_DAYS = 7
private val Duration.inWholeWeeks: Long
  get() = this.inWholeDays / WEEK_IN_DAYS
