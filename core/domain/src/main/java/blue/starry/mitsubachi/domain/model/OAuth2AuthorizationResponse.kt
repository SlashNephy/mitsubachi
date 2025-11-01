package blue.starry.mitsubachi.domain.model

data class OAuth2AuthorizationResponse(
  val code: String,
  val state: String,
  val request: OAuth2AuthorizationRequest,
)
