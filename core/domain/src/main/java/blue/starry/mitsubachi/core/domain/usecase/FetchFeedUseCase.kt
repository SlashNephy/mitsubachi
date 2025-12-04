package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.error.UnauthorizedError
import blue.starry.mitsubachi.core.domain.model.CheckIn
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchFeedUseCase @Inject constructor(
  private val foursquare: FoursquareApiClient,
  private val swarm: SwarmApiClient,
  private val foursquareAccountRepository: FoursquareAccountRepository,
  private val userSettingsRepository: UserSettingsRepository,
) {
  suspend operator fun invoke(
    limit: Int? = null,
    after: ZonedDateTime? = null,
  ): List<CheckIn> {
    val account = foursquareAccountRepository.primary.first() ?: throw UnauthorizedError()
    val settings = userSettingsRepository.flow(account).first()

    if (!settings.useSwarmCompatibilityMode || settings.swarmOAuthToken.isNullOrBlank()) {
      return foursquare.getRecentCheckIns(
        limit = limit,
        after = after,
      )
    }

    return swarm.getRecentActivities(
      oauthToken = settings.swarmOAuthToken,
      uniqueDevice = settings.uniqueDevice,
      wsid = settings.wsid,
      userAgent = settings.userAgent,
      afterTimestamp = after?.toEpochSecond(),
    )
  }
}
