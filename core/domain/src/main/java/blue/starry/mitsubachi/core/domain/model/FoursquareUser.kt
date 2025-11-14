package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class FoursquareUser(
  val id: String,
  val handle: String?,
  val firstName: String,
  val displayName: String,
  val iconUrl: String,
  val countryCode: String?,
  val state: String?,
  val city: String?,
  val address: String?,
  val gender: String?,
  val isPrivateProfile: Boolean,
)
