package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.core.data.network.model.SwarmRecentActivitiesResponse
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import de.jensklingenberg.ktorfit.core.NoDelegation
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Tag

interface SwarmNetworkApi : @NoDelegation NetworkApi {
  @GET("/activities/recent")
  suspend fun getRecentActivities(
    @Query attachmentsLimit: Int = 4,
    @Query idealLimit: Int = 3,
    @Query earliestAttachments: Boolean = false,
    @Query afterMarker: String? = null,
    @Query limit: Int = 20,
    @Query ll: String? = null,
    @Query llAcc: Float = 100.0f,
    @Query alt: Double? = null,
    @Query uniqueDevice: String? = null,
    @Query includeStatus: Boolean = true,
    @Query leaderboard: Boolean = true,
    @Query updatesAfterMarker: String? = null,
    @Query afterTimestamp: Long? = null,
    @Query("oauth_token") oauthToken: String,
    @Query v: String = "20220328",
    @Query wsid: String? = null,
    @Query csid: Int = 7,
    @Query m: String = "swarm",
    @Header("x-fs-consumer") xFsConsumer: String = "53",
    @Header("user-agent") userAgent: String,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<SwarmRecentActivitiesResponse>
}
