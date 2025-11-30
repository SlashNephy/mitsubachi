package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.model.FoursquareAddCheckInResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareCheckInResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareSearchVenueRecommendationsResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareSearchVenuesResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserCheckinsResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserPhotosResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserResponse
import blue.starry.mitsubachi.core.data.network.model.FoursquareUserVenueHistoriesResponse
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import de.jensklingenberg.ktorfit.core.NoDelegation
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Tag
import io.ktor.client.request.forms.MultiPartFormDataContent

@Suppress("TooManyFunctions")
interface FoursquareNetworkApi : @NoDelegation NetworkApi {
  @GET("/checkins/recent")
  suspend fun getRecentCheckIns(
    @Query limit: Int?,
    @Query afterTimeStamp: Long?,
    @Query ll: String?,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareRecentCheckinsResponse>

  @GET("/checkins/{checkInId}")
  suspend fun getCheckIn(
    @Path checkInId: String,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareCheckInResponse>

  @GET("/venues/search")
  suspend fun searchNearbyVenues(
    @Query query: String?,
    @Query ll: String?,
    @Query near: String?,
    @Query radius: Int?,
    @Query categoryId: String?,
    @Query limit: Int?,
    @Query url: String?,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareSearchVenuesResponse>

  @GET("/search/recommendations")
  suspend fun searchVenueRecommendations(
    @Query query: String?,
    @Query ll: String?,
    @Query radius: Int?,
    @Query sw: String?,
    @Query ne: String?,
    @Query near: String?,
    @Query section: String?,
    @Query categoryId: String?,
    @Query novelty: String?,
    @Query friendVisits: String?,
    @Query time: String?,
    @Query day: String?,
    @Query lastVenue: String?,
    @Query openNow: Boolean?,
    @Query price: String?,
    @Query saved: Boolean?,
    @Query sortByDistance: Boolean?,
    @Query sortByPopularity: Boolean?,
    @Query limit: Int?,
    @Query offset: Int?,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareSearchVenueRecommendationsResponse>

  @POST("/checkins/add")
  suspend fun addCheckIn(
    @Query venueId: String,
    @Query shout: String?,
    @Query broadcast: String?,
    @Query stickerId: String?,
  ): FoursquareApiResponse<FoursquareAddCheckInResponse>

  @POST("/checkins/{checkInId}/update")
  suspend fun updateCheckIn(@Path checkInId: String, @Query shout: String?)

  @POST("/checkins/{checkInId}/delete")
  suspend fun deleteCheckIn(@Path checkInId: String)

  @GET("/users/{userId}")
  suspend fun getUser(
    @Path userId: String,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareUserResponse>

  @GET("/users/{userId}/venuehistory")
  suspend fun getUserVenueHistories(
    @Path userId: String,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareUserVenueHistoriesResponse>

  @POST("/photos/add")
  suspend fun addPhoto(
    @Query("checkinId") checkInId: String,
    @Query public: Int,
    @Body body: MultiPartFormDataContent,
  )

  @POST("/checkins/{checkInId}/like")
  suspend fun likeCheckIn(@Path checkInId: String)

  @GET("/users/{userId}/checkins")
  suspend fun getUserCheckIns(
    @Path userId: String,
    @Query limit: Int?,
    @Query offset: Int?,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareUserCheckinsResponse>

  @GET("/users/{userId}/photos")
  suspend fun getUserPhotos(
    @Path userId: String,
    @Query limit: Int?,
    @Query offset: Int?,
    @Tag policy: FetchPolicy,
  ): FoursquareApiResponse<FoursquareUserPhotosResponse>
}
