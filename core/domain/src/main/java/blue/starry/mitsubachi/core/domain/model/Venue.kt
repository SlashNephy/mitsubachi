package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable
import blue.starry.mitsubachi.core.common.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Immutable
@Serializable
data class Venue(
  val id: String,
  val name: String,
  val location: VenueLocation,
  @Serializable(with = ZonedDateTimeSerializer::class)
  val createdAt: ZonedDateTime,
  val categories: List<VenueCategory>,
)

val Venue.primaryCategory: VenueCategory?
  get() = categories.firstOrNull { it.isPrimary } ?: categories.firstOrNull()

@Immutable
@Serializable
data class VenueLocation(
  val latitude: Double,
  val longitude: Double,
  val distance: Int?,
  val country: String,
  val countryCode: String,
  val postalCode: String?,
  val state: String?,
  val city: String?,
  val address: String?,
  val crossStreet: String?,
  val neighborhood: String?,
)

@Immutable
@Serializable
data class VenueCategory(
  val id: String,
  val name: String,
  val iconUrl: String,
  val isPrimary: Boolean,
)
