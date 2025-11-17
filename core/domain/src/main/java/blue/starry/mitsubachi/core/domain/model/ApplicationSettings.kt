package blue.starry.mitsubachi.core.domain.model

data class ApplicationSettings(
  val isFirebaseCrashlyticsEnabled: Boolean,
) {
  companion object {
    val Default = ApplicationSettings(
      isFirebaseCrashlyticsEnabled = true,
    )
  }
}
