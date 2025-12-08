package blue.starry.mitsubachi.core.ui.common.deeplink

sealed interface DeepLink {
  data class CheckIn(val id: String) : DeepLink
  data object CreateCheckIn : DeepLink
}
