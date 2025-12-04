package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.settings.R

@Composable
internal fun DataCollectionSection(
  applicationSettings: ApplicationSettings,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
) {
  SettingSection(title = stringResource(R.string.data_collection_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.bug_report),
      headline = {
        Text(stringResource(R.string.optin_firebase_crashlytics_headline))
      },
      supporting = {
        Text(stringResource(R.string.optin_firebase_crashlytics_supporting))
      },
      trailing = {
        Switch(
          checked = applicationSettings.isFirebaseCrashlyticsEnabled,
          onCheckedChange = {
            onChangeApplicationSettings { settings ->
              settings.copy(
                isFirebaseCrashlyticsEnabled = it,
              )
            }
          },
        )
      },
    )
  }
}
