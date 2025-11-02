package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.FoursquareAccount
import blue.starry.mitsubachi.domain.model.OAuth2AuthorizationRequest
import blue.starry.mitsubachi.domain.model.OAuth2AuthorizationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeginAuthorizationUseCase @Inject constructor(
  private val foursquare: FoursquareOAuth2Client,
) {
  operator fun invoke(): OAuth2AuthorizationRequest {
    return foursquare.createAuthorizationRequest()
  }
}

@Singleton
class FinishAuthorizationUseCase @Inject constructor(
  private val foursquare: FoursquareOAuth2Client,
  private val foursquareAccountRepository: FoursquareAccountRepository,
) {
  suspend operator fun invoke(
    response: OAuth2AuthorizationResponse,
  ): FoursquareAccount {
    val account = foursquare.exchangeToken(response)
    foursquareAccountRepository.update(account)

    return account
  }
}
