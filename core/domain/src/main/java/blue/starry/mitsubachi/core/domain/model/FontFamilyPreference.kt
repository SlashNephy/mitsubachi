package blue.starry.mitsubachi.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FontFamilyPreference(val fontName: String) {
  companion object {
    val IBMPlexSans = FontFamilyPreference("IBM Plex Sans")
  }
}
