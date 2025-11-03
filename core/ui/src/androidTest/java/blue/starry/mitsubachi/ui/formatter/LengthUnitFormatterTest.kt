package blue.starry.mitsubachi.ui.formatter

import android.icu.util.ULocale
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class LengthUnitFormatterTest {
  @Test
  fun testMeasurementSystemSI() {
    val cases = mapOf(
      4 to "0 m",
      5 to "10 m",
      14 to "10 m",
      15 to "20 m",
      900 to "900 m",
      949 to "950 m",
      994 to "990 m",
      999 to "1.0 km",
      1_000 to "1.0 km",
      2_040 to "2.0 km",
      2_050 to "2.1 km",
      9_900 to "9.9 km",
      9_940 to "9.9 km",
      9_949 to "10 km",
      9_950 to "10 km",
      9_999 to "10 km",
      10_000 to "10 km",
      12_000 to "12 km",
      240_000 to "240 km",
      3_600_000 to "3,600 km",
    )

    for ((input, expected) in cases.entries) {
      val actual = LengthUnitFormatter.formatMeters(input, ULocale.JAPAN)
      assertEquals(expected, actual, "input: $input")
    }
  }

  @Test
  fun testMeasurementSystemUS() {
    val cases = mapOf(
      4 to "0 ft",
      5 to "10 ft",
      14 to "10 ft",
      15 to "20 ft",
      490 to "490 ft",
      494 to "490 ft",
      495 to "0.1 mi",
      499 to "0.1 mi",
      500 to "0.1 mi",
      5_270 to "1.0 mi",
      5_280 to "1.0 mi",
      10_560 to "2.0 mi",
      52_000 to "9.8 mi",
      52_360 to "9.9 mi",
      52_370 to "9.9 mi",
      52_799 to "10 mi",
      52_800 to "10 mi",
      105_600 to "20 mi",
      1_267_200 to "240 mi",
    )

    for ((input, expected) in cases.entries) {
      val actual = LengthUnitFormatter.formatFeet(input, ULocale.US)
      assertEquals(expected, actual, "input: $input")
    }
  }

  @Test
  fun testMeasurementSystemUK() {
    val cases = mapOf(
      4 to "0 yd",
      5 to "10 yd",
      14 to "10 yd",
      15 to "20 yd",
      190 to "190 yd",
      194 to "190 yd",
      195 to "0.1 mi",
      199 to "0.1 mi",
      200 to "0.1 mi",
      1_750 to "1.0 mi",
      1_760 to "1.0 mi",
      3_520 to "2.0 mi",
      17_300 to "9.8 mi",
      17_420 to "9.9 mi",
      17_510 to "9.9 mi",
      17_599 to "10 mi",
      17_600 to "10 mi",
      35_200 to "20 mi",
      422_400 to "240 mi",
    )

    for ((input, expected) in cases.entries) {
      val actual = LengthUnitFormatter.formatYards(input, ULocale.UK)
      assertEquals(expected, actual, "input: $input")
    }
  }
}
