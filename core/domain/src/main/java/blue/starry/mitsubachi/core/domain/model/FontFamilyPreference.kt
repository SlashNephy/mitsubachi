package blue.starry.mitsubachi.core.domain.model

sealed interface FontFamilyPreference {
  data object Default : FontFamilyPreference

  data class GoogleFont(val fontName: String) : FontFamilyPreference
}
