package blue.starry.mitsubachi.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FoursquareAccount(
  val id: String,
  val displayName: String,
  val iconUrl: String,
  val accessToken: String,
  val authStateJson: String,
)
