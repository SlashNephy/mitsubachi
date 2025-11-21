package blue.starry.mitsubachi.core.domain.model

sealed interface FontFamilyPreference {
  val fontName: String

  data object IBMPlexSans : FontFamilyPreference {
    override val fontName: String = "IBM Plex Sans"
  }

  data class GoogleFont(override val fontName: String) : FontFamilyPreference
}
