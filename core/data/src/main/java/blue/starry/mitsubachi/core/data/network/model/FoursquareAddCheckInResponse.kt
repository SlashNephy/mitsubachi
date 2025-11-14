package blue.starry.mitsubachi.core.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareAddCheckInResponse(
  @SerialName("checkin") val checkIn: FoursquareCheckIn,
)
