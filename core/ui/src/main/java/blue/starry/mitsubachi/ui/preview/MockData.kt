package blue.starry.mitsubachi.ui.preview

import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.model.VenueCategory
import blue.starry.mitsubachi.domain.model.VenueLocation
import java.time.ZonedDateTime

@Suppress("MemberVisibilityCanBePrivate")
object MockData {
  private val now = ZonedDateTime.now()

  val PrimaryVenueCategory = VenueCategory(
    id = "category",
    name = "宮殿",
    iconUrl = "https://example.com/icon-primary.png",
    isPrimary = true,
  )

  val SecondaryVenueCategory = VenueCategory(
    id = "category",
    name = "国立公園",
    iconUrl = "https://example.com/icon-secondary.png",
    isPrimary = false,
  )

  val VenueLocation = VenueLocation(
    latitude = 35.6830452,
    longitude = 139.755504,
    distance = 12_345,
    country = "日本",
    countryCode = "JP",
    postalCode = "100-8111",
    state = "東京都",
    city = "千代田区",
    address = "千代田1-1",
    crossStreet = null,
    neighborhood = null,
  )

  val Venue = Venue(
    id = "venue",
    name = "皇居",
    location = VenueLocation,
    createdAt = now,
    categories = listOf(
      PrimaryVenueCategory,
      SecondaryVenueCategory,
    ),
  )
}
