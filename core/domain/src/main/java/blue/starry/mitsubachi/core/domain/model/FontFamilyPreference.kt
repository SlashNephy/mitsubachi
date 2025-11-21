package blue.starry.mitsubachi.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface FontFamilyPreference {
  val fontName: String

  @Serializable
  data object IBMPlexSans : FontFamilyPreference {
    override val fontName: String = "IBM Plex Sans"
  }

  @Serializable
  data class GoogleFont(override val fontName: String) : FontFamilyPreference
}
