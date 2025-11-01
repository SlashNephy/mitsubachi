package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.FoursquareAccount
import blue.starry.mitsubachi.domain.model.OAuth2AuthorizationRequest
import blue.starry.mitsubachi.domain.model.OAuth2AuthorizationResponse

interface FoursquareOAuth2Client {
  fun createAuthorizationRequest(): OAuth2AuthorizationRequest
  suspend fun exchangeToken(response: OAuth2AuthorizationResponse): FoursquareAccount
}
