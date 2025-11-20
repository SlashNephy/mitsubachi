package blue.starry.mitsubachi.core.data.repository.model

import blue.starry.mitsubachi.core.data.datastore.ApplicationSettings
import kotlin.time.Duration.Companion.milliseconds
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings as DomainApplicationSettings

internal fun ApplicationSettings.toDomain(): DomainApplicationSettings {
  return DomainApplicationSettings(
    isFirebaseCrashlyticsEnabled = if (hasIsFirebaseCrashlyticsEnabled()) {
      isFirebaseCrashlyticsEnabled
    } else {
      DomainApplicationSettings.Default.isFirebaseCrashlyticsEnabled
    },
    isWidgetUpdateOnUnmeteredNetworkOnlyEnabled = if (hasIsWidgetUpdateOnUnmeteredNetworkOnlyEnabled()) {
      isWidgetUpdateOnUnmeteredNetworkOnlyEnabled
    } else {
      DomainApplicationSettings.Default.isWidgetUpdateOnUnmeteredNetworkOnlyEnabled
    },
    widgetUpdateInterval = if (hasWidgetUpdateIntervalMillis()) {
      widgetUpdateIntervalMillis.milliseconds
    } else {
      DomainApplicationSettings.Default.widgetUpdateInterval
    },
  )
}

internal fun DomainApplicationSettings.toEntity(): ApplicationSettings {
  return ApplicationSettings.newBuilder()
    .setIsFirebaseCrashlyticsEnabled(isFirebaseCrashlyticsEnabled)
    .setIsWidgetUpdateOnUnmeteredNetworkOnlyEnabled(isWidgetUpdateOnUnmeteredNetworkOnlyEnabled)
    .setWidgetUpdateIntervalMillis(widgetUpdateInterval.inWholeMilliseconds)
    .build()
}
