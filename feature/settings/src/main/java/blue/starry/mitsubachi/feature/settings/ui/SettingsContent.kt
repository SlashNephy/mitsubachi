@file:Suppress("TooManyFunctions")

package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.UserSettings
import blue.starry.mitsubachi.core.ui.compose.preview.MockData
import blue.starry.mitsubachi.core.ui.compose.preview.PreviewImageProvider
import blue.starry.mitsubachi.feature.settings.ui.section.AccountSection
import blue.starry.mitsubachi.feature.settings.ui.section.AdvancedSection
import blue.starry.mitsubachi.feature.settings.ui.section.AppearanceSection
import blue.starry.mitsubachi.feature.settings.ui.section.DataCollectionSection
import blue.starry.mitsubachi.feature.settings.ui.section.VersionSection
import blue.starry.mitsubachi.feature.settings.ui.section.WidgetSection
import kotlin.time.Duration

@Suppress("LongMethod")
@Composable
fun SettingsContent(
  account: FoursquareAccount,
  applicationSettings: ApplicationSettings,
  userSettings: UserSettings,
  applicationConfig: ApplicationConfig,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
  onChangeUserSettings: ((UserSettings) -> UserSettings) -> Unit,
  onSignOut: () -> Unit,
  onUpdateWidgetSchedule: () -> Unit,
  formatDuration: (Duration) -> String,
  modifier: Modifier = Modifier,
) {
  Surface(modifier = modifier) {
    LazyColumn(
      modifier = Modifier.padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        AccountSection(
          account = account,
          onClickSignOut = onSignOut,
        )
      }

      item {
        AppearanceSection(
          applicationSettings = applicationSettings,
          onChangeApplicationSettings = onChangeApplicationSettings,
        )
      }

      item {
        WidgetSection(
          applicationSettings = applicationSettings,
          onChangeApplicationSettings = onChangeApplicationSettings,
          onChangeWidgetSettings = onUpdateWidgetSchedule,
          formatDuration = formatDuration,
        )
      }

      item {
        DataCollectionSection(
          applicationSettings = applicationSettings,
          onChangeApplicationSettings = onChangeApplicationSettings,
        )
      }

      if (applicationSettings.isAdvancedSettingsAvailable) {
        item {
          AdvancedSection(
            userSettings = userSettings,
            onChangeUserSettings = onChangeUserSettings,
          )
        }
      }

      item {
        VersionSection(
          applicationSettings = applicationSettings,
          applicationConfig = applicationConfig,
          onChangeApplicationSettings = onChangeApplicationSettings,
        )
      }
    }
  }
}

@Preview
@Composable
private fun SettingsContentPreview() {
  PreviewImageProvider {
    SettingsContent(
      account = MockData.PrimaryFoursquareAccount,
      applicationSettings = ApplicationSettings.Default.copy(isAdvancedSettingsAvailable = true),
      userSettings = UserSettings.Default,
      applicationConfig = MockData.ApplicationConfig,
      onChangeApplicationSettings = { ApplicationSettings.Default },
      onChangeUserSettings = { UserSettings.Default },
      onSignOut = {},
      onUpdateWidgetSchedule = {},
      formatDuration = { "1 hour" },
    )
  }
}
