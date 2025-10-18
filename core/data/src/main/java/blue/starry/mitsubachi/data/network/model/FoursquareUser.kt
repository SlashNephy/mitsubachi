package blue.starry.mitsubachi.data.network.model

import kotlinx.serialization.Serializable
import blue.starry.mitsubachi.domain.model.FoursquareUser as DomainFoursquareUser


@Serializable
data class FoursquareUser(
  val address: String?,
  val birthday: Int?,
  val city: String?,
  val countryCode: String,
  val displayName: String,
  val firstName: String,
  val followingRelationship: String?,
  val gender: String,
  val handle: String,
  val id: String,
  val lastName: String?,
  val photo: Photo,
  val privateProfile: Boolean,
  val relationship: String?,
  val state: String?,
) {
  @Serializable
  data class Checkins(
    val count: Int,
  )

  @Serializable
  data class Photo(
    val prefix: String,
    val suffix: String,
  )
}

internal fun FoursquareUser.toDomain(iconSize: String = "120"): DomainFoursquareUser {
  return DomainFoursquareUser(
    id = id,
    handle = handle,
    firstName = firstName,
    displayName = displayName,
    // https://docs.foursquare.com/developer/reference/places-photos-guide
    iconUrl = "${photo.prefix}${iconSize}${photo.suffix}",
    countryCode = countryCode,
    state = state,
    city = city,
    address = address,
    gender = gender,
    isPrivateProfile = privateProfile,
  )
}
