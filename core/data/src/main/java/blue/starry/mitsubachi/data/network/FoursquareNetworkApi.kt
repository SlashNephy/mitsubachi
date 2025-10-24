package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareAddCheckInResponse
import blue.starry.mitsubachi.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareSearchVenuesResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserDetailsResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent

interface FoursquareNetworkApi {
  @GET("/checkins/recent")
  suspend fun getRecentCheckIns(
    @Query limit: Int?,
    @Query afterTimeStamp: Long?,
    @Query ll: String?,
  ): FoursquareApiResponse<FoursquareRecentCheckinsResponse>

  @GET("/venues/search")
  suspend fun searchNearVenues(
    @Query ll: String,
    @Query query: String?,
  ): FoursquareApiResponse<FoursquareSearchVenuesResponse>

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

  @GET("/users/self")
  suspend fun getSelfUserDetails(): FoursquareApiResponse<FoursquareUserDetailsResponse>

  @POST("/photos/add")
  suspend fun addPhoto(
    @Query("checkinId") checkInId: String,
    @Query public: Int,
    @Body body: MultiPartFormDataContent,
  )

  @POST("/checkins/{checkInId}/like")
  suspend fun likeCheckIn(@Path checkInId: String)

  @POST("/checkins/{checkInId}/unlike")
  suspend fun unlikeCheckIn(@Path checkInId: String)
}
