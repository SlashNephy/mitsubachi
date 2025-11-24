package blue.starry.mitsubachi.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface FontFamilyPreference {
  val fontName: String

  @Serializable
  data class GoogleFont(override val fontName: String) : FontFamilyPreference {
    companion object {
      val IBMPlexSans = GoogleFont("IBM Plex Sans")
    }
  }
}
