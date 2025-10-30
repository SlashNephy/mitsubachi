package blue.starry.mitsubachi.domain.model.foursquare

import blue.starry.mitsubachi.domain.model.Venue

data class VenueHistory(
  val venue: Venue,
  val count: Int,
)
