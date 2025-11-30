package blue.starry.mitsubachi.core.ui.compose.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.core.os.ConfigurationCompat
import blue.starry.mitsubachi.core.domain.model.FontFamilyPreference
import java.util.Locale

@Composable
fun rememberTypography(fontFamilyPreference: FontFamilyPreference? = null): Typography {
  val configuration = LocalConfiguration.current
  return remember(configuration, fontFamilyPreference) {
    val locale = ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
    val typography = when (locale.language) {
      Locale.JAPANESE.language -> {
        MitsubachiTypography.Japanese
      }

      Locale.KOREAN.language -> {
        MitsubachiTypography.Korean
      }

      else -> {
        MitsubachiTypography.English
      }
    }

    when (fontFamilyPreference) {
      null -> {
        typography
      }

      is FontFamilyPreference.GoogleFont -> {
        typography.with(
          fontFamily = FontFamily(MitsubachiFont.from(fontFamilyPreference.fontName)),
        )
      }
    }
  }
}
