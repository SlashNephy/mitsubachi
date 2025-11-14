package blue.starry.mitsubachi.core.data.repository.model

import blue.starry.mitsubachi.core.data.datastore.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings as DomainApplicationSettings

private val defaultApplicationSettings = DomainApplicationSettings(
  isFirebaseCrashlyticsEnabled = true,
)

internal fun ApplicationSettings.toDomain(): DomainApplicationSettings {
  return DomainApplicationSettings(
    isFirebaseCrashlyticsEnabled = if (hasIsFirebaseCrashlyticsEnabled()) {
      isFirebaseCrashlyticsEnabled
    } else {
      defaultApplicationSettings.isFirebaseCrashlyticsEnabled
    },
  )
}

internal fun DomainApplicationSettings.toEntity(): ApplicationSettings {
  return ApplicationSettings.newBuilder()
    .setIsFirebaseCrashlyticsEnabled(isFirebaseCrashlyticsEnabled)
    .build()
}
