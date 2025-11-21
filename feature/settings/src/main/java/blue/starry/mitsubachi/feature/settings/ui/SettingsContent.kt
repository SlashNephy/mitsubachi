@file:Suppress("TooManyFunctions")

package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.ColorSchemePreference
import blue.starry.mitsubachi.core.domain.model.FontFamilyPreference
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.feature.settings.R
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Suppress("LongMethod")
@Composable
fun SettingsContent(
  applicationSettings: ApplicationSettings,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
  onSignOut: () -> Unit,
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
        },
        onChangeWidgetUpdateInterval = {
          onChangeApplicationSettings { settings ->
            settings.copy(
              widgetUpdateInterval = it,
            )
          }
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

@Composable
private fun AccountSection(onClickSignOut: () -> Unit) {
  val showLogoutDialog = remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.account_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.AutoMirrored.Filled.Logout),
      headline = {
        Text(stringResource(R.string.logout_button))
      },
      modifier = Modifier.clickable(onClick = { showLogoutDialog.value = true }),
    )
  }

  if (showLogoutDialog.value) {
    LogoutConfirmationDialog(
      onConfirm = {
        showLogoutDialog.value = false
        onClickSignOut()
      },
      onDismiss = {
        showLogoutDialog.value = false
      },
    )
  }
}

@Composable
private fun LogoutConfirmationDialog(
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(R.string.logout_button),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      Text(
        text = stringResource(R.string.logout_confirm_body),
        style = MaterialTheme.typography.bodyMedium,
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(onClick = onConfirm) {
        Text(text = stringResource(R.string.logout_button))
      }
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetSection(
  applicationSettings: ApplicationSettings,
  onChangeIsWidgetUpdateOnUnmeteredNetworkOnlyEnabled: (Boolean) -> Unit,
  onChangeWidgetUpdateInterval: (Duration) -> Unit,
  formatDuration: (Duration) -> String,
) {
  var showIntervalDialog by remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.widget_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.NetworkWifi),
      headline = {
        Text(stringResource(R.string.widget_update_on_wifi_only_headline))
      },
      supporting = {
        Text(stringResource(R.string.widget_update_on_wifi_only_supporting))
      },
      trailing = {
        Switch(
          checked = applicationSettings.isWidgetUpdateOnUnmeteredNetworkOnlyEnabled,
          onCheckedChange = onChangeIsWidgetUpdateOnUnmeteredNetworkOnlyEnabled,
        )
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.Timelapse),
      headline = {
        Text(stringResource(R.string.widget_update_interval_headline))
      },
      supporting = {
        Text(stringResource(R.string.widget_update_interval_supporting))
      },
      trailing = {
        Text(formatDuration(applicationSettings.widgetUpdateInterval))
      },
      modifier = Modifier.clickable(onClick = { showIntervalDialog = true }),
    )
  }

  if (showIntervalDialog) {
    WidgetUpdateIntervalDialog(
      interval = applicationSettings.widgetUpdateInterval,
      onConfirm = {
        showIntervalDialog = false
        onChangeWidgetUpdateInterval(it)
      },
      onDismiss = {
        showIntervalDialog = false
      },
      formatDuration = formatDuration,
    )
  }
}

@Composable
private fun WidgetUpdateIntervalDialog(
  interval: Duration,
  onConfirm: (Duration) -> Unit,
  onDismiss: () -> Unit,
  formatDuration: (Duration) -> String,
) {
  var newInterval by remember { mutableStateOf(interval) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(R.string.widget_update_interval_headline),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        DurationSlider(
          value = newInterval,
          min = 15.minutes,
          max = 24.hours,
          onChange = {
            newInterval = it
          },
        )

        Text(formatDuration(newInterval))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(newInterval) }) {
        Text(text = stringResource(R.string.save_button))
      }
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationSlider(
  value: Duration,
  min: Duration,
  max: Duration,
  onChange: (Duration) -> Unit,
) {
  Slider(
    value = value.inWholeSeconds.toFloat(),
    valueRange = min.inWholeSeconds.toFloat()..max.inWholeSeconds.toFloat(),
    onValueChange = {
      onChange(it.roundToLong().seconds)
    },
  )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSection(
  applicationSettings: ApplicationSettings,
  onChangeIsDynamicColorEnabled: (Boolean) -> Unit,
  onChangeColorSchemePreference: (ColorSchemePreference) -> Unit,
  onChangeFontFamilyPreference: (FontFamilyPreference) -> Unit,
) {
  var showColorSchemeDialog by remember { mutableStateOf(false) }
  var showFontFamilyDialog by remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.appearance_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.ColorLens),
      headline = {
        Text(stringResource(R.string.dynamic_color_headline))
      },
      supporting = {
        Text(stringResource(R.string.dynamic_color_supporting))
      },
      trailing = {
        Switch(
          checked = applicationSettings.isDynamicColorEnabled,
          onCheckedChange = onChangeIsDynamicColorEnabled,
        )
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.DarkMode),
      headline = {
        Text(stringResource(R.string.color_scheme_headline))
      },
      supporting = {
        Text(stringResource(R.string.color_scheme_supporting))
      },
      trailing = {
        Text(
          when (applicationSettings.colorSchemePreference) {
            ColorSchemePreference.Light -> stringResource(R.string.color_scheme_light)
            ColorSchemePreference.Dark -> stringResource(R.string.color_scheme_dark)
            ColorSchemePreference.System -> stringResource(R.string.color_scheme_system)
          },
        )
      },
      modifier = Modifier.clickable(onClick = { showColorSchemeDialog = true }),
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.FontDownload),
      headline = {
        Text(stringResource(R.string.font_family_headline))
      },
      supporting = {
        Text(stringResource(R.string.font_family_supporting))
      },
      trailing = {
        Text(getFontFamilyName(applicationSettings.fontFamilyPreference))
      },
      modifier = Modifier.clickable(onClick = { showFontFamilyDialog = true }),
    )
  }

  if (showColorSchemeDialog) {
    ColorSchemeDialog(
      currentPreference = applicationSettings.colorSchemePreference,
      onConfirm = {
        showColorSchemeDialog = false
        onChangeColorSchemePreference(it)
      },
      onDismiss = {
        showColorSchemeDialog = false
      },
    )
  }

  if (showFontFamilyDialog) {
    FontFamilyDialog(
      currentPreference = applicationSettings.fontFamilyPreference,
      onConfirm = {
        showFontFamilyDialog = false
        onChangeFontFamilyPreference(it)
      },
      onDismiss = {
        showFontFamilyDialog = false
      },
    )
  }
}

@Composable
private fun getFontFamilyName(fontFamilyPreference: FontFamilyPreference): String {
  return when (fontFamilyPreference) {
    FontFamilyPreference.IBMPlexSans -> stringResource(R.string.font_family_ibm_plex_sans)
    FontFamilyPreference.Roboto -> stringResource(R.string.font_family_roboto)
    FontFamilyPreference.NotoSans -> stringResource(R.string.font_family_noto_sans)
    FontFamilyPreference.OpenSans -> stringResource(R.string.font_family_open_sans)
    FontFamilyPreference.Lato -> stringResource(R.string.font_family_lato)
    FontFamilyPreference.Montserrat -> stringResource(R.string.font_family_montserrat)
    FontFamilyPreference.Poppins -> stringResource(R.string.font_family_poppins)
    FontFamilyPreference.Oswald -> stringResource(R.string.font_family_oswald)
    FontFamilyPreference.SourceSansPro -> stringResource(R.string.font_family_source_sans_pro)
    FontFamilyPreference.Raleway -> stringResource(R.string.font_family_raleway)
  }
}

@Composable
private fun ColorSchemeDialog(
  currentPreference: ColorSchemePreference,
  onConfirm: (ColorSchemePreference) -> Unit,
  onDismiss: () -> Unit,
) {
  var selectedPreference by remember { mutableStateOf(currentPreference) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(R.string.color_scheme_headline),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      Column {
        ColorSchemePreference.entries.forEach { preference ->
          SettingItem(
            leadingIcon = SettingItem.LeadingIcon.None,
            headline = {
              Text(
                when (preference) {
                  ColorSchemePreference.Light -> stringResource(R.string.color_scheme_light)
                  ColorSchemePreference.Dark -> stringResource(R.string.color_scheme_dark)
                  ColorSchemePreference.System -> stringResource(R.string.color_scheme_system)
                },
              )
            },
            trailing = {
              RadioButton(
                selected = selectedPreference == preference,
                onClick = { selectedPreference = preference },
              )
            },
            modifier = Modifier.clickable { selectedPreference = preference },
          )
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(selectedPreference) }) {
        Text(text = stringResource(R.string.save_button))
      }
    },
  )
}

@Composable
private fun FontFamilyDialog(
  currentPreference: FontFamilyPreference,
  onConfirm: (FontFamilyPreference) -> Unit,
  onDismiss: () -> Unit,
) {
  var selectedPreference by remember { mutableStateOf(currentPreference) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(R.string.font_family_headline),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      Column {
        FontFamilyPreference.entries.forEach { preference ->
          SettingItem(
            leadingIcon = SettingItem.LeadingIcon.None,
            headline = {
              Text(getFontFamilyName(preference))
            },
            trailing = {
              RadioButton(
                selected = selectedPreference == preference,
                onClick = { selectedPreference = preference },
              )
            },
            modifier = Modifier.clickable { selectedPreference = preference },
          )
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(selectedPreference) }) {
        Text(text = stringResource(R.string.save_button))
      }
    },
  )
}

@Composable
private fun DataCollectionSection(
  applicationSettings: ApplicationSettings,
  onChangeIsFirebaseCrashlyticsEnabled: (Boolean) -> Unit,
) {
  SettingSection(title = stringResource(R.string.data_collection_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(Icons.Default.BugReport),
      headline = {
        Text(stringResource(R.string.optin_firebase_crashlytics_headline))
      },
      supporting = {
        Text(stringResource(R.string.optin_firebase_crashlytics_supporting))
      },
      trailing = {
        Switch(
          checked = applicationSettings.isFirebaseCrashlyticsEnabled,
          onCheckedChange = onChangeIsFirebaseCrashlyticsEnabled,
        )
      },
    )
  }
}

@Preview
@Composable
private fun SettingsContentPreview() {
  SettingsContent(
    applicationSettings = ApplicationSettings.Default,
    onChangeApplicationSettings = { ApplicationSettings.Default },
    onSignOut = {},
    formatDuration = { "1 hour" },
  )
}
