package blue.starry.mitsubachi.data.repository.model

import blue.starry.mitsubachi.data.datastore.ApplicationSettings
import blue.starry.mitsubachi.domain.model.ApplicationSettings as DomainApplicationSettings

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
