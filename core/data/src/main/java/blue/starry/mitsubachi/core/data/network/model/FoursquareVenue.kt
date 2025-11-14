package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueCategory
import blue.starry.mitsubachi.core.domain.model.VenueLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class FoursquareVenue(
  val categories: List<Category>,
  val closed: Boolean?,
  val createdAt: Long, // 1302357899
  val dislike: Boolean?,
  val id: String, // 4e74d9c588775d593db096c1
  val like: Boolean?,
  val location: Location,
  @SerialName("private") val isPrivate: Boolean?,
  val name: String, // LIQUID LOFT
  val ok: Boolean?,
) {
  @Serializable
  data class Category(
    val categoryCode: Int,
    val icon: Icon,
    val id: String,
    val mapDarkTextColor: String, // #d5d7da
    val mapIcon: String, // fsq-default
    val mapTextColor: String, // #72767f
    val name: String,
    val pluralName: String?,
    val primary: Boolean,
    val shortName: String?,
  ) {
    @Serializable
    data class Icon(
      val prefix: String, // https://ss3.4sqi.net/img/categories_v2/nightlife/nightclub_
      val suffix: String, // .png
    )
  }

  @Serializable
  data class Location(
    val address: String?,
    val cc: String,
    val city: String?,
    val country: String,
    val crossStreet: String?,
    val distance: Int?,
    val formattedAddress: List<String>?,
    val isFuzzed: Boolean?,
    val lat: Double,
    val lng: Double,
    val neighborhood: String?,
    val postalCode: String?,
    val state: String?,
  )
}

fun FoursquareVenue.toDomain(): Venue {
  return Venue(
    id = id,
    name = name,
    location = location.toDomain(),
    createdAt = Instant.ofEpochSecond(createdAt).atZone(ZoneId.systemDefault()),
    categories = categories.map { it.toDomain() },
  )
}

fun FoursquareVenue.Location.toDomain(): VenueLocation {
  return VenueLocation(
    latitude = lat,
    longitude = lng,
    distance = distance,
    country = country,
    countryCode = cc,
    postalCode = postalCode,
    state = state,
    city = city,
    address = address,
    crossStreet = crossStreet,
    neighborhood = neighborhood,
  )
}

fun FoursquareVenue.Category.toDomain(iconSize: String = "120"): VenueCategory {
  return VenueCategory(
    id = id,
    name = name,
    // https://docs.foursquare.com/developer/reference/places-photos-guide#category-icons
    iconUrl = "${icon.prefix}${iconSize}${icon.suffix}",
    isPrimary = primary,
  )
}
