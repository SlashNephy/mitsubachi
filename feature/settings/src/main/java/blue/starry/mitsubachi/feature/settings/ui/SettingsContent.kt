@file:Suppress("TooManyFunctions")

package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.feature.settings.ui.section.AccountSection
import blue.starry.mitsubachi.feature.settings.ui.section.AppearanceSection
import blue.starry.mitsubachi.feature.settings.ui.section.DataCollectionSection
import blue.starry.mitsubachi.feature.settings.ui.section.WidgetSection
import kotlin.time.Duration

@Suppress("LongMethod")
@Composable
fun SettingsContent(
  applicationSettings: ApplicationSettings,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
  onSignOut: () -> Unit,
  onUpdateWidgetSchedule: () -> Unit,
  formatDuration: (Duration) -> String,
) {
  Surface(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier.padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      AccountSection(
        onClickSignOut = onSignOut,
      )

      AppearanceSection(
        applicationSettings = applicationSettings,
        onChangeIsDynamicColorEnabled = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              isDynamicColorEnabled = it,
            )
          }
        },
        onChangeColorSchemePreference = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              colorSchemePreference = it,
            )
          }
        },
        onChangeFontFamilyPreference = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              fontFamilyPreference = it,
            )
          }
        },
      )

      WidgetSection(
        applicationSettings = applicationSettings,
        onChangeIsWidgetUpdateOnUnmeteredNetworkOnlyEnabled = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              isWidgetUpdateOnUnmeteredNetworkOnlyEnabled = it,
            )
          }
          onUpdateWidgetSchedule()
        },
        onChangeWidgetUpdateInterval = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              widgetUpdateInterval = it,
            )
          }
          onUpdateWidgetSchedule()
        },
        formatDuration = formatDuration,
      )

      DataCollectionSection(
        applicationSettings = applicationSettings,
        onChangeIsFirebaseCrashlyticsEnabled = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              isFirebaseCrashlyticsEnabled = it,
            )
          }
        },
      )
    }
  }
}

@Preview
@Composable
private fun SettingsContentPreview() {
  SettingsContent(
    applicationSettings = ApplicationSettings.Default,
    onChangeApplicationSettings = { ApplicationSettings.Default },
    onSignOut = {},
    onUpdateWidgetSchedule = {},
    formatDuration = { "1 hour" },
  )
}
