package blue.starry.mitsubachi.domain.usecase

import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeginAuthorizationUseCase @Inject constructor(
  private val foursquare: FoursquareOAuth2Client,
) {
  operator fun invoke(): Intent {
    return foursquare.createAuthorizationIntent()
  }
}

@Singleton
class FinishAuthorizationUseCase @Inject constructor(
  private val foursquare: FoursquareOAuth2Client,
  private val foursquareAccountRepository: FoursquareAccountRepository,
) {
  suspend operator fun invoke(authorizationResult: Intent) {
    val account = foursquare.exchangeToken(authorizationResult)

    foursquareAccountRepository.update(account)
  }
}
