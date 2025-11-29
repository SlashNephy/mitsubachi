package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.error.UnauthorizedError
import blue.starry.mitsubachi.core.domain.model.CheckIn
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchFeedUseCase @Inject constructor(
  private val foursquare: FoursquareApiClient,
  private val swarm: SwarmApiClient,
  private val foursquareAccountRepository: FoursquareAccountRepository,
  private val userSettingsRepository: UserSettingsRepository,
) {
  suspend operator fun invoke(): List<CheckIn> {
    val account = foursquareAccountRepository.primary.first() ?: throw UnauthorizedError()
    val settings = userSettingsRepository.flow(account).first()

    if (!settings.useSwarmCompatibilityMode || settings.swarmOAuthToken.isBlank()) {
      return foursquare.getRecentCheckIns()
    }

    return swarm.getRecentActivities(
      oauthToken = settings.swarmOAuthToken,
      uniqueDevice = settings.uniqueDevice,
      wsid = settings.wsid,
      userAgent = settings.userAgent,
    )
  }
}
