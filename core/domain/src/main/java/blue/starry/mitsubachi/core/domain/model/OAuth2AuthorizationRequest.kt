package blue.starry.mitsubachi.core.domain.model

data class OAuth2AuthorizationRequest(
  val authorizeUrl: String,
  val redirectScheme: String,
  val state: String,
  val codeVerifier: String,
)
