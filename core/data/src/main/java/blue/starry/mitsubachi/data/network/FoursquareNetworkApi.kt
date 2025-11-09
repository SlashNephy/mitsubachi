package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareAddCheckInResponse
import blue.starry.mitsubachi.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareSearchVenueRecommendationsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareSearchVenuesResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserCheckinsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserPhotosResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserVenueHistoriesResponse
import de.jensklingenberg.ktorfit.core.NoDelegation
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent

@Suppress("TooManyFunctions")
interface FoursquareNetworkApi : @NoDelegation NetworkApi {
  @GET("/checkins/recent")
  suspend fun getRecentCheckIns(
    @Query limit: Int?,
    @Query afterTimeStamp: Long?,
    @Query ll: String?,
  ): FoursquareApiResponse<FoursquareRecentCheckinsResponse>

  @GET("/venues/search")
  suspend fun searchNearbyVenues(
    @Query query: String? = null,
    @Query ll: String? = null,
    @Query near: String? = null,
    @Query radius: Int? = null,
    @Query categoryId: String? = null,
    @Query limit: Int? = null,
    @Query url: String? = null,
  ): FoursquareApiResponse<FoursquareSearchVenuesResponse>

  @GET("/search/recommendations")
  suspend fun searchVenueRecommendations(
    @Query query: String? = null,
    @Query ll: String? = null,
    @Query radius: Int? = null,
    @Query sw: String? = null,
    @Query ne: String? = null,
    @Query near: String? = null,
    @Query section: String? = null,
    @Query categoryId: String? = null,
    @Query novelty: String? = null,
    @Query friendVisits: String? = null,
    @Query time: String? = null,
    @Query day: String? = null,
    @Query lastVenue: String? = null,
    @Query openNow: Boolean? = null,
    @Query price: String? = null,
    @Query saved: Boolean? = null,
    @Query sortByDistance: Boolean? = null,
    @Query sortByPopularity: Boolean? = null,
    @Query limit: Int? = null,
    @Query offset: Int? = null,
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
  suspend fun getUser(@Path userId: String): FoursquareApiResponse<FoursquareUserResponse>

  @GET("/users/{userId}/venuehistory")
  suspend fun getUserVenueHistories(@Path userId: String): FoursquareApiResponse<FoursquareUserVenueHistoriesResponse>

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
  ): FoursquareApiResponse<FoursquareUserCheckinsResponse>

  @GET("/users/{userId}/photos")
  suspend fun getUserPhotos(
    @Path userId: String,
    @Query limit: Int?,
    @Query offset: Int?,
  ): FoursquareApiResponse<FoursquareUserPhotosResponse>
}
