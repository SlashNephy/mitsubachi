package blue.starry.mitsubachi.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SwarmRecentActivitiesResponse(
  val activities: Activities,
  val leaderboard: Leaderboard,
) {
  @Serializable
  data class Activities(
    val count: Int, // 20
    val items: List<Item>,
    val moreData: Boolean, // true
  ) {
    @Serializable
    data class Item(
      val checkin: FoursquareCheckIn,
      val type: String, // checkin
    )
  }

  @Serializable
  data class Leaderboard(
    val playerCount: Int, // 20
    val ranking: Int, // 6
    val rankingString: String, // 6位
    val score: Int, // 238
    val user: FoursquareUser,
  )
}
