package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable
import blue.starry.mitsubachi.core.common.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Immutable
@Serializable
data class CheckIn(
  val id: String,
  val venue: Venue,
  val user: FoursquareUser?,
  val coin: Int,
  val sticker: String?,
  val message: String?,
  val photos: List<Photo>,
  @Serializable(with = ZonedDateTimeSerializer::class) val timestamp: ZonedDateTime,
  val isLiked: Boolean,
  val likeCount: Int,
  val source: Source?,
  val isMeyer: Boolean,
)

@Immutable
@Serializable
data class Photo(
  val id: String,
  val url: String,
  val width: Int,
  val height: Int,
)
