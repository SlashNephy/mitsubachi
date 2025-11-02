package blue.starry.mitsubachi.domain.model

data class ApplicationConfig(
  val versionName: String,
  val versionCode: Int,
  val isDebugBuild: Boolean,
  val foursquareClientId: String,
  val foursquareClientSecret: String,
  val foursquareRedirectUri: String,
)

val ApplicationConfig.isProductionBuild: Boolean
  get() = !isDebugBuild
