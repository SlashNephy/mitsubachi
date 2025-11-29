package blue.starry.mitsubachi.core.domain.model

data class UserSettings(
  val useSwarmCompatibilityMode: Boolean,
  val swarmOAuthToken: String?,
  val uniqueDevice: String?,
  val wsid: String?,
  val userAgent: String?,
) {
  companion object {
    val Default = UserSettings(
      useSwarmCompatibilityMode = false,
      swarmOAuthToken = null,
      uniqueDevice = null,
      wsid = null,
      userAgent = null,
    )
  }
}
