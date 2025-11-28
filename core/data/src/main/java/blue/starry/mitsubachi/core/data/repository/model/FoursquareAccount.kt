package blue.starry.mitsubachi.core.data.repository.model

import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount as DomainFoursquareAccount

internal fun FoursquareAccount.toDomain(): DomainFoursquareAccount {
  return DomainFoursquareAccount(
    id = id,
    displayName = displayName,
    iconUrl = iconUrl,
    accessToken = accessToken,
    isPrimary = isPrimary,
  )
}

internal fun DomainFoursquareAccount.toEntity(): FoursquareAccount {
  return FoursquareAccount(
    id = id,
    displayName = displayName,
    iconUrl = iconUrl,
    accessToken = accessToken,
    isPrimary = isPrimary,
  )
}
