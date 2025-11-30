package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.model.foursquare.Photo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class FoursquareUserPhotosResponse(
  val photos: Photos,
  val totalCount: Int,
) {
  @Serializable
  data class Photos(
    val count: Int,
    val items: List<Item>,
  ) {
    @Serializable
    data class Item(
      @SerialName("checkin") val checkIn: CheckIn,
      val createdAt: Long,
      val height: Int,
      val id: String,
      val prefix: String,
      val suffix: String,
      val venue: FoursquareVenue,
      val width: Int,
    ) {
      @Serializable
      data class CheckIn(
        val id: String,
      )
    }
  }
}

fun FoursquareUserPhotosResponse.Photos.Item.toDomain(size: String = "original"): Photo {
  return Photo(
    checkInId = checkIn.id,
    createdAt = Instant.ofEpochSecond(createdAt).atZone(ZoneId.systemDefault()),
    height = height,
    id = id,
    url = "${prefix}${size}$suffix",
    venue = venue.toDomain(),
    width = width,
  )
}
