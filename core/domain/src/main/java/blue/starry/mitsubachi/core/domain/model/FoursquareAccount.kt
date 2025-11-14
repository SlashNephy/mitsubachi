package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FoursquareAccount(
  val id: String,
  val displayName: String,
  val iconUrl: String,
  val accessToken: String,
)
