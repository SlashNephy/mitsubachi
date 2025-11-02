package blue.starry.mitsubachi.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class VenueRecommendation(
  val id: String,
  val venue: Venue,
  val photo: Photo?,
  val tips: List<Tip>,
) {
  @Immutable
  @Serializable
  data class Photo(
    val id: String,
    val prefix: String,
    val suffix: String,
    val width: Int,
    val height: Int,
  )

  @Immutable
  @Serializable
  data class Tip(
    val id: String,
    val text: String,
    val userName: String?,
  )
}

fun VenueRecommendation.Photo.url(size: Int = 300): String {
  return "${prefix}${size}x${size}$suffix"
}
