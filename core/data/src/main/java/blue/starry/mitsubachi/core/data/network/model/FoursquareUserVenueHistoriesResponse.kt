package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareUserVenueHistoriesResponse(
  val venues: Venues,
) {
  @Serializable
  data class Venues(
    val count: Int,
    val items: List<Item>,
  ) {
    @Serializable
    data class Item(
      val beenHere: Int,
      val venue: FoursquareVenue,
    )
  }
}

fun FoursquareUserVenueHistoriesResponse.Venues.Item.toDomain(): VenueHistory {
  return VenueHistory(
    venue = venue.toDomain(),
    count = beenHere,
  )
}
