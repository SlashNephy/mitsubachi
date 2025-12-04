package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.settings.R
import kotlinx.coroutines.Job
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WidgetSection(
  applicationSettings: ApplicationSettings,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Job,
  onChangeWidgetSettings: () -> Unit,
  formatDuration: (Duration) -> String,
) {
  var showIntervalDialog by remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.widget_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.network_wifi),
      headline = {
        Text(stringResource(R.string.widget_update_on_wifi_only_headline))
      },
      supporting = {
        Text(stringResource(R.string.widget_update_on_wifi_only_supporting))
      },
      trailing = {
        Switch(
          checked = applicationSettings.isWidgetUpdateOnUnmeteredNetworkOnlyEnabled,
          onCheckedChange = {
            onChangeApplicationSettings { settings ->
              settings.copy(
                isWidgetUpdateOnUnmeteredNetworkOnlyEnabled = it,
              )
            }.invokeOnCompletion {
              onChangeWidgetSettings()
            }
          },
        )
      },
    )

    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.timelapse),
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
        onChangeApplicationSettings { settings ->
          settings.copy(
            widgetUpdateInterval = it,
          )
        }.invokeOnCompletion {
          onChangeWidgetSettings()
        }
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
