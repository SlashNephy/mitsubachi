package blue.starry.mitsubachi.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareTokenResponse(
  @SerialName("access_token") val accessToken: String,
)
