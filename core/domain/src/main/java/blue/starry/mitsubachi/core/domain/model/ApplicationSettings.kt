package blue.starry.mitsubachi.core.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class ApplicationSettings(
  val isFirebaseCrashlyticsEnabled: Boolean,
  val isWidgetUpdateOnUnmeteredNetworkOnlyEnabled: Boolean,
  val widgetUpdateInterval: Duration,
  val isDynamicColorEnabled: Boolean,
  val colorSchemePreference: ColorSchemePreference,
  val fontFamilyPreference: FontFamilyPreference,
  val isAdvancedSettingsAvailable: Boolean,
  val isBackgroundLocationTrackingEnabled: Boolean,
) {
  companion object {
    val Default = ApplicationSettings(
      isFirebaseCrashlyticsEnabled = true,
      isWidgetUpdateOnUnmeteredNetworkOnlyEnabled = false,
      widgetUpdateInterval = 1.hours,
      isDynamicColorEnabled = false,
      colorSchemePreference = ColorSchemePreference.System,
      fontFamilyPreference = FontFamilyPreference.Default,
      isAdvancedSettingsAvailable = false,
      isBackgroundLocationTrackingEnabled = false,
    )
  }
}
