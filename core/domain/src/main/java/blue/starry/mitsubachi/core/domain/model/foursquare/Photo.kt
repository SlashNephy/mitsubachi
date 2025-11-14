package blue.starry.mitsubachi.core.domain.model.foursquare

import blue.starry.mitsubachi.core.domain.model.Venue
import java.time.ZonedDateTime

data class Photo(
  val checkInId: String,
  val createdAt: ZonedDateTime,
  val height: Int,
  val id: String,
  val url: String,
  val venue: Venue,
  val width: Int,
)
