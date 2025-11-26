package blue.starry.mitsubachi.core.ui.compose.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import blue.starry.mitsubachi.core.ui.compose.R

object MitsubachiFont {
  val IBMPlexSans by lazy {
    GoogleFont("IBM Plex Sans").toFontVariants()
  }

  val IBMPlexSansJP by lazy {
    GoogleFont("IBM Plex Sans JP").toFontVariants()
  }

  val IBMPlexSansKR by lazy {
    GoogleFont("IBM Plex Sans KR").toFontVariants()
  }

  fun from(fontName: String): List<Font> {
    return GoogleFont(fontName).toFontVariants()
  }

  private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
  )

  private fun GoogleFont.toFontVariants(): List<Font> {
    return listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.Bold).map { weight ->
      androidx.compose.ui.text.googlefonts.Font(
        googleFont = this,
        fontProvider = googleFontProvider,
        weight = weight,
      )
    }
  }
}
