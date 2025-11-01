package blue.starry.mitsubachi.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class VenueRecommendation(
  val id: String,
  val venue: Venue,
  val photo: VenueRecommendationPhoto?,
  val tips: List<VenueRecommendationTip>,
)

@Immutable
@Serializable
data class VenueRecommendationPhoto(
  val id: String,
  val prefix: String,
  val suffix: String,
  val width: Int,
  val height: Int,
) {
  fun url(size: Int = 300): String {
    return "${prefix}${size}x${size}${suffix}"
  }
}

@Immutable
@Serializable
data class VenueRecommendationTip(
  val id: String,
  val text: String,
  val userName: String?,
)
