package blue.starry.mitsubachi.core.ui.compose.preview

import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.FoursquareUser
import blue.starry.mitsubachi.core.domain.model.Photo
import blue.starry.mitsubachi.core.domain.model.Source
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueCategory
import blue.starry.mitsubachi.core.domain.model.VenueLocation
import java.time.ZonedDateTime

@Suppress("MemberVisibilityCanBePrivate", "StringLiteralDuplication")
object MockData {
  private val now = ZonedDateTime.now()

  val ApplicationConfig = ApplicationConfig(
    applicationId = "blue.starry.mitsubachi.local",
    versionName = "1.0",
    versionCode = 123,
    buildType = "debug",
    flavor = "local",
    foursquareClientId = "client_id",
    foursquareClientSecret = "client_secret",
    foursquareRedirectUri = "blue.starry.mitsubachi.local://oauth2/callback",
  )

  val PrimaryFoursquareAccount = FoursquareAccount(
    id = "account",
    displayName = "山田太郎",
    email = "taro@example.com",
    iconUrl = "https://example.com/photo.png",
    accessToken = "xxx",
    isPrimary = true,
  )

  val PrimaryVenueCategory = VenueCategory(
    id = "primary-category",
    name = "宮殿",
    iconUrl = "https://example.com/icon-primary.png",
    isPrimary = true,
  )

  val SecondaryVenueCategory = VenueCategory(
    id = "secondary-category",
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

  val User = FoursquareUser(
    id = "user",
    handle = "handle",
    firstName = "太郎",
    displayName = "山田太郎",
    iconUrl = "https://fastly.4sqi.net/img/user/original/169641728_E4167jqJ_ZVweL-ylJRwFG7qhxdmD-IwwmQ-00B9IFxOTHmW2LggCyyzpq9fiDCAPGFDyFIIF.jpg",
    countryCode = "JP",
    state = "東京都",
    city = "千代田区",
    address = "丸の内2-4-1",
    gender = null,
    isPrivateProfile = false,
    email = "taro@example.com",
  )

  val FriendUser = FoursquareUser(
    id = "friend",
    handle = "friend",
    firstName = "太郎",
    displayName = "山田太郎",
    iconUrl = "https://fastly.4sqi.net/img/user/original/169641728_E4167jqJ_ZVweL-ylJRwFG7qhxdmD-IwwmQ-00B9IFxOTHmW2LggCyyzpq9fiDCAPGFDyFIIF.jpg",
    countryCode = "JP",
    state = "東京都",
    city = "千代田区",
    address = "丸の内2-4-1",
    gender = null,
    isPrivateProfile = false,
    email = "taro@example.com",
  )

  val CheckIn = CheckIn(
    id = "check-in",
    venue = Venue,
    user = User,
    createdBy = FriendUser,
    coin = 0,
    sticker = null,
    message = "今日は仕事が早く終わったので、久しぶりにスタバでコーヒーを飲んでリフレッシュしました！新しい季節のフレーバーも試してみたけど、美味しかったです。皆さんもぜひ訪れてみてください！",
    photos = listOf(
      Photo(
        id = "id",
        url = "https://example.com/photo.png",
        width = 1080,
        height = 1080,
      ),
    ),
    timestamp = now,
    isLiked = true,
    likeCount = 5,
    source = Source(name = "Swarm for iOS", url = "https://www.swarmapp.com"),
    isMeyer = true,
  )
}
