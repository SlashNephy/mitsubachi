package blue.starry.mitsubachi.core.data.repository.model

import blue.starry.mitsubachi.core.data.database.entity.UserSettings
import blue.starry.mitsubachi.core.domain.model.UserSettings as DomainUserSettings

internal fun UserSettings.Payload.toDomain(): DomainUserSettings {
  return DomainUserSettings(
    useSwarmCompatibilityMode = useSwarmCompatibilityMode,
    swarmOAuthToken = swarmOAuthToken,
    uniqueDevice = uniqueDevice,
    wsid = wsid,
    userAgent = userAgent,
  )
}

internal fun DomainUserSettings.toEntity(): UserSettings.Payload {
  return UserSettings.Payload(
    useSwarmCompatibilityMode = useSwarmCompatibilityMode,
    swarmOAuthToken = swarmOAuthToken,
    uniqueDevice = uniqueDevice,
    wsid = wsid,
    userAgent = userAgent,
  )
}
