package blue.starry.mitsubachi.domain.model

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
data class CheckIn(
  val id: String,
  val venue: Venue,
  val user: FoursquareUser,
  val coin: Int,
  val sticker: String?,
  val message: String?,
  val photos: List<Photo>,
  val timestamp: ZonedDateTime,
  val isLiked: Boolean,
  val likeCount: Int,
  val source: Source,
  val isMeyer: Boolean,
)

@Immutable
data class Photo(
  val id: String,
  val url: String,
  val width: Int,
  val height: Int,
)
