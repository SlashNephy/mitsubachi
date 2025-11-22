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
    if (fontFamilyPreference != null) {
      // Use explicit font preference
      getTypographyForFont(fontFamilyPreference)
    } else {
      // Use locale-based font
      val locale = ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
      when (locale.language) {
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
    }
  }
}

private fun getTypographyForFont(fontFamilyPreference: FontFamilyPreference): Typography {
  val fontFamily = if (fontFamilyPreference.fontName == FontFamilyPreference.IBMPlexSans.fontName) {
    FontFamily(
      listOf(
        MitsubachiFont.IBMPlexSans,
        MitsubachiFont.IBMPlexSansJP,
        MitsubachiFont.IBMPlexSansKR,
      ).flatten(),
    )
  } else {
    FontFamily(
      MitsubachiFont.fromName(fontFamilyPreference.fontName),
    )
  }
  return Typography().with(fontFamily)
}
