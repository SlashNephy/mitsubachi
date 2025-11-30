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
  val contact: Contact?,
  val closed: Boolean?,
  val createdAt: Long, // 1302357899
  val explanation: String?,
  val header: String?,
  val id: String, // 4e74d9c588775d593db096c1
  val like: Boolean?,
  val location: Location,
  @SerialName("private") val isPrivate: Boolean?,
  val name: String,
  val ratingCount: Int?,
  val stats: Stats?,
  val url: String?,
  val verified: Boolean?,
  val visitorsCount: Int?,
) {
  @Serializable
  data class Category(
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
  data class Contact(
    val formattedPhone: String?, // 047-405-2440
    val phone: String?, // 0474052440
  )

  @Serializable
  data class Location(
    val address: String?,
    val cc: String,
    val city: String?,
    val contextLine: String?,
    val country: String,
    val crossStreet: String?,
    val distance: Int?,
    val isFuzzed: Boolean?,
    val lat: Double,
    val lng: Double,
    val neighborhood: String?,
    val postalCode: String?,
    val state: String?,
  )

  @Serializable
  data class Stats(
    val checkinsCount: Int, // 383
    val tipCount: Int, // 1
    val usersCount: Int, // 92
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
