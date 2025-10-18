package blue.starry.mitsubachi.domain.usecase

import android.content.Intent
import blue.starry.mitsubachi.domain.model.FoursquareAccount

interface FoursquareOAuth2Client {
  fun createAuthorizationIntent(): Intent
  suspend fun exchangeToken(authorizationResult: Intent): FoursquareAccount
}
