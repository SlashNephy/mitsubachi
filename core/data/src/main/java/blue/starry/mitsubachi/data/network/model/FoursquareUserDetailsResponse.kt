package blue.starry.mitsubachi.data.network.model


import kotlinx.serialization.Serializable

@Serializable
data class FoursquareUserDetailsResponse(
  val user: FoursquareUser,
)
