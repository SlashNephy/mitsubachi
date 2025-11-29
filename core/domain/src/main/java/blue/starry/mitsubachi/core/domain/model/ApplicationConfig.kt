package blue.starry.mitsubachi.core.domain.model

data class ApplicationConfig(
  val applicationId: String,
  val versionName: String,
  val versionCode: Int,
  val buildType: String,
  val flavor: String,
  val foursquareClientId: String,
  val foursquareClientSecret: String,
  val foursquareRedirectUri: String,
)
