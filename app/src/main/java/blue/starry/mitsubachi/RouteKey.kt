package blue.starry.mitsubachi

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import blue.starry.mitsubachi.domain.model.Venue
import kotlinx.serialization.Serializable

sealed interface RouteKey : NavKey {
  @Immutable
  @Serializable
  data object Welcome : RouteKey

  @Immutable
  @Serializable
  data object Home : RouteKey

  @Immutable
  @Serializable
  data object NearbyVenues : RouteKey

  @Immutable
  @Serializable
  data class CreateCheckIn(val venue: Venue) : RouteKey

  @Immutable
  @Serializable
  data class CheckIn(val id: Int) : RouteKey

  @Immutable
  @Serializable
  data class User(val id: Int) : RouteKey

  @Immutable
  @Serializable
  data object Settings : RouteKey
}
