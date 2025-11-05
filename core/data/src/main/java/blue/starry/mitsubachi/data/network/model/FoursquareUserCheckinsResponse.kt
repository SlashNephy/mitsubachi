package blue.starry.mitsubachi.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class FoursquareUserCheckinsResponse(
  val checkins: CheckIns,
) {
  @Serializable
  data class CheckIns(
    val count: Int,
    val items: List<FoursquareCheckIn>,
  )
}
