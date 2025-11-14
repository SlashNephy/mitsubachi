package blue.starry.mitsubachi.core.domain.model.foursquare

import blue.starry.mitsubachi.core.domain.model.Venue

data class VenueHistory(
  val venue: Venue,
  val count: Int,
)
