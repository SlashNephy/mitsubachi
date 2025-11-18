package blue.starry.mitsubachi.core.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class ApplicationSettings(
  val isFirebaseCrashlyticsEnabled: Boolean,
  val isWidgetUpdateOnUnmeteredNetworkOnlyEnabled: Boolean,
  val widgetUpdateInterval: Duration,
) {
  companion object {
    val Default = ApplicationSettings(
      isFirebaseCrashlyticsEnabled = true,
      isWidgetUpdateOnUnmeteredNetworkOnlyEnabled = false,
      widgetUpdateInterval = 1.hours,
    )
  }
}
