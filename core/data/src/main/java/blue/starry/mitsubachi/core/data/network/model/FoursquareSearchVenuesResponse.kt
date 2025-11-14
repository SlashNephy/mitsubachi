package blue.starry.mitsubachi.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class FoursquareSearchVenuesResponse(
  val confident: Boolean? = null, // false
  val venues: List<FoursquareVenue>,
)
