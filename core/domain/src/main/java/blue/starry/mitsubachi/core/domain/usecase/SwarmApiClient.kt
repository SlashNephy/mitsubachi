package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.CheckInPage
import blue.starry.mitsubachi.core.domain.model.FetchPolicy

interface SwarmApiClient {
  suspend fun getRecentActivities(
    oauthToken: String,
    uniqueDevice: String? = null,
    wsid: String? = null,
    userAgent: String? = null,
    limit: Int? = null,
    beforeMarker: String? = null,
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): CheckInPage
}
