package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.usecase.FoursquareCheckInBroadcastFlag
import blue.starry.mitsubachi.core.domain.usecase.VenueRecommendationSection

internal fun FoursquareCheckInBroadcastFlag.toQuery(): String {
  return when (this) {
    FoursquareCheckInBroadcastFlag.Public -> "public"
    FoursquareCheckInBroadcastFlag.Private -> "private"
    FoursquareCheckInBroadcastFlag.Facebook -> "facebook"
    FoursquareCheckInBroadcastFlag.Twitter -> "twitter"
  }
}

internal fun VenueRecommendationSection.toQuery(): String {
  return when (this) {
    VenueRecommendationSection.Food -> "food"
    VenueRecommendationSection.Drinks -> "drinks"
    VenueRecommendationSection.Coffee -> "coffee"
    VenueRecommendationSection.Shops -> "shops"
    VenueRecommendationSection.Arts -> "arts"
    VenueRecommendationSection.Outdoors -> "outdoors"
    VenueRecommendationSection.Sights -> "sights"
    VenueRecommendationSection.Trending -> "trending"
    VenueRecommendationSection.NextVenues -> "nextVenues"
    VenueRecommendationSection.TopPicks -> "topPicks"
  }
}
