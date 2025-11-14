package blue.starry.mitsubachi.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class FoursquareUserResponse(
  val user: FoursquareUser,
)
