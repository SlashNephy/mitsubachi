package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueCategory
import blue.starry.mitsubachi.core.domain.model.VenueLocation
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StayVenueCategoriesTest {
  @Test
  fun matchesHotelCategoryInJapanese() {
    // 実データで最多の宿泊系カテゴリ（ユニーク 48 ベニュー）
    assertTrue(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d1fa931735", "ホテル", "travel/hotel_")),
      ),
    )
  }

  @Test
  fun matchesHotelCategoryInEnglish() {
    // 同じカテゴリ ID がロケール次第で英語名でも返る。名前が変わっても判定が変わらないこと
    assertTrue(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d1fa931735", "Hotel", "travel/hotel_")),
      ),
    )
  }

  @Test
  fun matchesOtherLodgingCategoriesFoundInRealData() {
    assertTrue(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d1f8931735", "B&Bホテル", "travel/bedandbreakfast_")),
      ),
    )
    assertTrue(
      StayVenueCategories.matches(
        venue(category("4f4530a74b9074f6e4fb0100", "下宿屋", "travel/hotel_")),
      ),
    )
    assertTrue(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d12f951735", "リゾート", "travel/resort_")),
      ),
    )
    assertTrue(
      StayVenueCategories.matches(
        venue(category("63be6904847c3692a84b9c27", "ロッジ", "parks_outdoors/hikingtrail_")),
      ),
    )
    assertTrue(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d1eb941735", "スキーロッジ", "parks_outdoors/ski_lodge_")),
      ),
    )
  }

  @Test
  fun matchesUnknownCategoryWithLodgingIcon() {
    // ID を列挙できていない宿泊カテゴリが増えても、アイコンのパスで拾えること
    assertTrue(
      StayVenueCategories.matches(
        venue(category("0123456789abcdef01234567", "カプセルホテル", "travel/hotel_")),
      ),
    )
  }

  @Test
  fun doesNotMatchNonLodgingTravelCategories() {
    // 同じ travel 配下のアイコンでも宿泊でないものは拾わないこと
    assertFalse(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d129951735", "鉄道駅", "travel/trainstation_")),
      ),
    )
    assertFalse(
      StayVenueCategories.matches(
        venue(category("4d954b16a243a5684b65b473", "サービスエリア", "travel/restarea_")),
      ),
    )
    assertFalse(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d1ed931735", "空港", "travel/airport_")),
      ),
    )
  }

  @Test
  fun doesNotMatchCategoriesSharingLodgeIcon() {
    // ロッジと同じ hikingtrail_ アイコンを使う公園系を拾わないこと
    assertFalse(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d159941735", "ハイキングコース", "parks_outdoors/hikingtrail_")),
      ),
    )
    assertFalse(
      StayVenueCategories.matches(
        venue(category("52e81612bcbc57f1066b7a21", "国立公園", "parks_outdoors/hikingtrail_")),
      ),
    )
  }

  @Test
  fun doesNotMatchLodgingAdjacentCategories() {
    assertFalse(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d160941735", "温泉", "parks_outdoors/hotspring_")),
      ),
    )
    assertFalse(
      StayVenueCategories.matches(
        venue(category("4bf58dd8d48988d1ed941735", "スパ", "shops/spa_")),
      ),
    )
  }

  @Test
  fun matchesWhenAnySecondaryCategoryIsLodging() {
    assertTrue(
      StayVenueCategories.matches(
        venue(
          category("4bf58dd8d48988d116941735", "バー", "nightlife/pub_", isPrimary = true),
          category("4bf58dd8d48988d1fa931735", "ホテル", "travel/hotel_", isPrimary = false),
        ),
      ),
    )
  }

  @Test
  fun doesNotMatchVenueWithoutCategories() {
    assertFalse(StayVenueCategories.matches(venue()))
  }

  private fun category(
    id: String,
    name: String,
    iconPath: String,
    isPrimary: Boolean = true,
  ): VenueCategory {
    return VenueCategory(
      id = id,
      name = name,
      // FoursquareVenue で prefix + サイズ + suffix に組み立てられた形
      iconUrl = "https://ss3.4sqi.net/img/categories_v2/${iconPath}64.png",
      isPrimary = isPrimary,
    )
  }

  private fun venue(vararg categories: VenueCategory): Venue {
    return Venue(
      id = "venue",
      name = "venue",
      location = VenueLocation(
        latitude = 35.0,
        longitude = 139.0,
        distance = null,
        country = "JP",
        countryCode = "JP",
        postalCode = null,
        state = "東京都",
        city = null,
        address = null,
        crossStreet = null,
        neighborhood = null,
      ),
      createdAt = ZonedDateTime.parse("2020-01-01T00:00:00+09:00"),
      categories = categories.toList(),
    )
  }
}
