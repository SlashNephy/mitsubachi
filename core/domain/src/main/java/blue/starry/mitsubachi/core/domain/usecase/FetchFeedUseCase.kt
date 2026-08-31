package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.error.UnauthorizedError
import blue.starry.mitsubachi.core.domain.model.CheckInPage
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
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
  /**
   * @param limit 1 ページあたりの取得件数
   * @param beforeMarker より古いページを取得するためのマーカー。null の場合は先頭から取得する
   * @param policy キャッシュの利用方針
   */
  suspend operator fun invoke(
    limit: Int? = null,
    beforeMarker: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): CheckInPage {
    val account = foursquareAccountRepository.primary.first() ?: throw UnauthorizedError()
    val settings = userSettingsRepository.flow(account).first()

    if (!settings.useSwarmCompatibilityMode || settings.swarmOAuthToken.isNullOrBlank()) {
      // Foursquare の /checkins/recent は limit のみを受け付ける固定長のフィードで、
      // offset・beforeTimestamp によるページングに対応していない。
      // そのため常に 1 ページで打ち切る。
      return CheckInPage(
        checkIns = if (beforeMarker != null) {
          emptyList()
        } else {
          foursquare.getRecentCheckIns(limit = limit, policy = policy)
        },
        nextMarker = null,
      )
    }

    return swarm.getRecentActivities(
      oauthToken = settings.swarmOAuthToken,
      uniqueDevice = settings.uniqueDevice,
      wsid = settings.wsid,
      userAgent = settings.userAgent,
      limit = limit,
      beforeMarker = beforeMarker,
      policy = policy,
    )
  }
}
