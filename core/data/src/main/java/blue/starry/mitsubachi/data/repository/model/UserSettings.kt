package blue.starry.mitsubachi.data.repository.model

import blue.starry.mitsubachi.data.datastore.UserSettings
import blue.starry.mitsubachi.domain.model.UserSettings as DomainUserSettings

private val defaultUserSettings = DomainUserSettings(
  some = 1,
)

internal fun UserSettings.toDomain(): DomainUserSettings {
  return DomainUserSettings(
    some = 1,
  )
}

internal fun DomainUserSettings.toEntity(): UserSettings {
  return UserSettings.newBuilder()
    .build()
}
