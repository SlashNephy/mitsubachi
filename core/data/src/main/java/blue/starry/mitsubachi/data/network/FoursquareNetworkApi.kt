package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.data.network.model.FoursquareRecentCheckinsResponse
import blue.starry.mitsubachi.data.network.model.FoursquareSearchVenuesResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserDetailsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface FoursquareNetworkApi {
  @GET("/checkins/recent")
  suspend fun getRecentCheckIns(): FoursquareApiResponse<FoursquareRecentCheckinsResponse>

  @GET("/venues/search")
  suspend fun searchNearVenues(
    @Query ll: String,
    @Query query: String?,
  ): FoursquareApiResponse<FoursquareSearchVenuesResponse>

  @POST("/checkins/add")
  suspend fun addCheckIn(@Query venueId: String, @Query shout: String?)

  @POST("/checkins/{checkInId}/update")
  suspend fun updateCheckIn(@Path checkInId: String, @Query shout: String?)

  @POST("/checkins/{checkInId}/delete")
  suspend fun deleteCheckIn(@Path checkInId: String)

  @GET("/users/self")
  suspend fun getSelfUserDetails(): FoursquareApiResponse<FoursquareUserDetailsResponse>
}
