package blue.starry.mitsubachi.data.network.model

import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.model.VenueCategory
import blue.starry.mitsubachi.domain.model.VenueLocation
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class FoursquareVenue(
  val categories: List<Category>,
  val createdAt: Long, // 1302357899
  val id: String, // 4e74d9c588775d593db096c1
  val location: Location,
  val name: String, // LIQUID LOFT
) {
  @Serializable
  data class Category(
    val icon: Icon,
    val id: String, // 4bf58dd8d48988d11f941735
    val name: String, // ナイトクラブ
    val primary: Boolean,
  ) {
    @Serializable
    data class Icon(
      val prefix: String, // https://ss3.4sqi.net/img/categories_v2/nightlife/nightclub_
      val suffix: String, // .png
    )
  }

  @Serializable
  data class Location(
    val address: String?, // 東3-16-6
    val cc: String, // JP
    val city: String?, // 東京
    val country: String, // 日本
    val crossStreet: String?, // LIQUIDROOM 2F
    val distance: Int? = null, // 19
    val formattedAddress: List<String>?,
    // val labeledLatLngs: List<LabeledLatLng>?,
    val lat: Double, // 35.64914546007966
    val lng: Double, // 139.71088252061986
    val neighborhood: String?, // 恵比寿
    val postalCode: String?, // 160-0021
    val state: String?, // 東京都
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
