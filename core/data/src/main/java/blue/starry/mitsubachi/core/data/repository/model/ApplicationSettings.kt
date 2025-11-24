package blue.starry.mitsubachi.core.data.repository.model

import blue.starry.mitsubachi.core.data.datastore.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.ColorSchemePreference
import blue.starry.mitsubachi.core.domain.model.FontFamilyPreference
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
    isDynamicColorEnabled = if (hasIsDynamicColorEnabled()) {
      isDynamicColorEnabled
    } else {
      DomainApplicationSettings.Default.isDynamicColorEnabled
    },
    colorSchemePreference = if (hasColorSchemePreference()) {
      ColorSchemePreference.entries.getOrNull(colorSchemePreference)
        ?: DomainApplicationSettings.Default.colorSchemePreference
    } else {
      DomainApplicationSettings.Default.colorSchemePreference
    },
    fontFamilyPreference = if (hasGoogleFont()) {
      FontFamilyPreference.GoogleFont(googleFont)
    } else {
      DomainApplicationSettings.Default.fontFamilyPreference
    },
  )
}

internal fun DomainApplicationSettings.toEntity(): ApplicationSettings {
  return ApplicationSettings.newBuilder()
    .setIsFirebaseCrashlyticsEnabled(isFirebaseCrashlyticsEnabled)
    .setIsWidgetUpdateOnUnmeteredNetworkOnlyEnabled(isWidgetUpdateOnUnmeteredNetworkOnlyEnabled)
    .setWidgetUpdateIntervalMillis(widgetUpdateInterval.inWholeMilliseconds)
    .setIsDynamicColorEnabled(isDynamicColorEnabled)
    .setColorSchemePreference(colorSchemePreference.ordinal)
    .apply {
      when (fontFamilyPreference) {
        is FontFamilyPreference.GoogleFont -> {
          setGoogleFont(fontFamilyPreference.fontName)
        }
      }
    }
    .build()
}
