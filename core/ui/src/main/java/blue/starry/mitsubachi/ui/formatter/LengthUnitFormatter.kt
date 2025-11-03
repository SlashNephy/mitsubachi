package blue.starry.mitsubachi.ui.formatter

import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.LocaleData
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import android.icu.util.ULocale
import tech.units.indriya.function.MultiplyConverter
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.TransformedUnit
import tech.units.indriya.unit.Units
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.measure.MetricPrefix
import javax.measure.Quantity
import javax.measure.Unit
import javax.measure.quantity.Length
import kotlin.math.roundToInt

object LengthUnitFormatter {
  fun formatMeters(meters: Number, uLocale: ULocale = ULocale.getDefault()): String {
    val quantity = Quantities.getQuantity(meters.roundToNearestTen(), Units.METRE)
    return format(quantity, uLocale)
  }

  fun formatFeet(feet: Number, uLocale: ULocale = ULocale.getDefault()): String {
    val quantity = Quantities.getQuantity(feet.roundToNearestTen(), CustomUnits.FOOT)
    return format(quantity, uLocale)
  }

  fun formatYards(yards: Number, uLocale: ULocale = ULocale.getDefault()): String {
    val quantity = Quantities.getQuantity(yards.roundToNearestTen(), CustomUnits.YARD)
    return format(quantity, uLocale)
  }

  private fun Number.roundToNearestTen(): Int {
    // 1の位で四捨五入
    return (toDouble() / 10.0).roundToInt() * 10
  }

  private fun format(quantity: Quantity<Length>, uLocale: ULocale): String {
    val system = uLocale.getDesiredMeasurementSystem()
    val desiredQuantity = quantity.toDesiredQuantity(system)

    val measure = desiredQuantity.toMeasure()
    val precision = measure.getAppropriatePrecision()

    return NumberFormatter
      .withLocale(uLocale)
      .unitWidth(NumberFormatter.UnitWidth.SHORT)
      .precision(precision)
      .roundingMode(RoundingMode.HALF_UP)
      .format(measure)
      .toString()
  }

  private fun ULocale.getDesiredMeasurementSystem(): LocaleData.MeasurementSystem {
    // Android 16 でユーザーが変更した測定単位はキーワードに格納されているようだった
    // API があるといいんだけど見つけられなかった。UnitPreferences というやつがあるっぽいが...
    // https://android.googlesource.com/platform/prebuilts/fullsdk/sources/+/refs/heads/androidx-tv-material-release/android-34/android/icu/impl/units/UnitPreferences.java
    val keyword = getKeywordValue("measure")
    return when (keyword) {
      "metric" -> {
        LocaleData.MeasurementSystem.SI
      }

      "ussystem" -> {
        LocaleData.MeasurementSystem.US
      }

      "imperial" -> {
        LocaleData.MeasurementSystem.UK
      }

      else -> {
        LocaleData.getMeasurementSystem(this)
      }
    }
  }

  // https://belief-driven-design.com/java-measurement-jsr-385-210f2/
  private object CustomUnits {
    val KILO_METRE: Unit<Length> = MetricPrefix.KILO(Units.METRE)

    private val INCH = TransformedUnit(
      "in",
      Units.METRE,
      MultiplyConverter.ofRational(
        BigInteger.valueOf(254),
        BigInteger.valueOf(10_000),
      ),
    )

    val FOOT = TransformedUnit(
      "ft",
      INCH,
      MultiplyConverter.of(12),
    )

    val MILE = TransformedUnit(
      "mi",
      FOOT,
      MultiplyConverter.of(5280),
    )

    val YARD = TransformedUnit(
      "yd",
      FOOT,
      MultiplyConverter.of(3),
    )
  }

  private fun Quantity<Length>.toDesiredQuantity(system: LocaleData.MeasurementSystem): Quantity<Length> {
    return when (system) {
      LocaleData.MeasurementSystem.SI -> {
        val meter = to(Units.METRE)
        if (meter.value.toDouble() < 1_000.0) {
          meter
        } else {
          meter.to(CustomUnits.KILO_METRE)
        }
      }

      LocaleData.MeasurementSystem.US -> {
        val foot = to(CustomUnits.FOOT)
        if (foot.value.toDouble() < 500.0) {
          foot
        } else {
          foot.to(CustomUnits.MILE)
        }
      }

      LocaleData.MeasurementSystem.UK -> {
        val yard = to(CustomUnits.YARD)
        if (yard.value.toDouble() < 200.0) {
          yard
        } else {
          yard.to(CustomUnits.MILE)
        }
      }

      else -> {
        error("Unsupported measurement system: $system")
      }
    }
  }

  private fun Quantity<Length>.toMeasure(): Measure {
    val unit = when {
      unit.isEquivalentTo(Units.METRE) -> {
        MeasureUnit.METER
      }

      unit.isEquivalentTo(CustomUnits.KILO_METRE) -> {
        MeasureUnit.KILOMETER
      }

      unit.isEquivalentTo(CustomUnits.FOOT) -> {
        MeasureUnit.FOOT
      }

      unit.isEquivalentTo(CustomUnits.MILE) -> {
        MeasureUnit.MILE
      }

      unit.isEquivalentTo(CustomUnits.YARD) -> {
        MeasureUnit.YARD
      }

      else -> {
        error("Unsupported unit: $unit")
      }
    }

    return Measure(value.toDouble(), unit)
  }

  private fun Measure.getAppropriatePrecision(): Precision {
    return when (unit) {
      // m, ft, yd 単位の場合は1の位を四捨五入する
      MeasureUnit.METER, MeasureUnit.FOOT, MeasureUnit.YARD -> {
        Precision.increment(BigDecimal(10))
      }

      // km, mi 単位の場合、小数点第1位を四捨五入して 10 未満は小数点第1位まで表示し、それ以上は整数で表示する
      MeasureUnit.KILOMETER, MeasureUnit.MILE -> {
        if (number.toDouble() < 9.95) {
          Precision.fixedFraction(1)
        } else {
          Precision.integer()
        }
      }

      else -> {
        error("Unsupported unit: $unit")
      }
    }
  }
}
