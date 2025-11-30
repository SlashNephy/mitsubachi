package blue.starry.mitsubachi.core.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareTokenResponse(
  @SerialName("access_token") val accessToken: String,
)
