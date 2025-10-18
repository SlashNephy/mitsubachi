package blue.starry.mitsubachi.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import blue.starry.mitsubachi.R

private val typography = Typography(
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp,
  ),
)

val provider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage = "com.google.android.gms",
  certificates = R.array.com_google_android_gms_fonts_certs,
)

val NotoSansJP = FontFamily(
  Font(
    googleFont = GoogleFont("Noto Sans JP"),
    fontProvider = provider,
  ),
)

val supportedLocales = arrayOf("en", "ja", "kr")

@Composable
fun rememberTypography(): Typography {
  val configuration = LocalConfiguration.current

  return remember(configuration) {
    val locale = ConfigurationCompat.getLocales(configuration).getFirstMatch(supportedLocales)

    val fontFamily = if (locale?.language == "ja") {
      NotoSansJP
    } else {
      FontFamily.Default
    }

    Typography(
      bodyLarge = typography.bodyLarge.copy(fontFamily = fontFamily),
      titleLarge = typography.titleLarge.copy(fontFamily = fontFamily),
    )
  }
}
