package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.ColorSchemePreference
import blue.starry.mitsubachi.core.domain.model.FontFamilyPreference
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.settings.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("LongMethod")
@Composable
internal fun AppearanceSection(
  applicationSettings: ApplicationSettings,
  onChangeIsDynamicColorEnabled: (Boolean) -> Unit,
  onChangeColorSchemePreference: (ColorSchemePreference) -> Unit,
  onChangeFontFamilyPreference: (FontFamilyPreference) -> Unit,
) {
  var showColorSchemeDialog by remember { mutableStateOf(false) }
  var showFontFamilyDialog by remember { mutableStateOf(false) }

  SettingSection(title = stringResource(R.string.appearance_section_title)) {
    item(
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.palette_filled),
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
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.dark_mode_filled),
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
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.font_download_filled),
      headline = {
        Text(stringResource(R.string.font_family_headline))
      },
      supporting = {
        Text(stringResource(R.string.font_family_supporting))
      },
      trailing = {
        Text(applicationSettings.fontFamilyPreference.fontName)
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
      Column(modifier = Modifier.selectableGroup()) {
        ColorSchemePreference.entries.forEach { preference ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
              selected = selectedPreference == preference,
              onClick = { selectedPreference = preference },
            )
            Text(
              when (preference) {
                ColorSchemePreference.Light -> stringResource(R.string.color_scheme_light)
                ColorSchemePreference.Dark -> stringResource(R.string.color_scheme_dark)
                ColorSchemePreference.System -> stringResource(R.string.color_scheme_system)
              },
            )
          }
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

@Suppress("LongMethod", "TooGenericExceptionCaught")
@Composable
private fun FontFamilyDialog(
  currentPreference: FontFamilyPreference,
  onConfirm: (FontFamilyPreference) -> Unit,
  onDismiss: () -> Unit,
) {
  var selectedFont by remember { mutableStateOf(currentPreference.fontName) }
  var fontList by remember { mutableStateOf<List<String>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    scope.launch {
      try {
        fontList = fetchGoogleFonts()
        isLoading = false
      } catch (_: Exception) {
        isLoading = false
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = stringResource(R.string.font_family_headline),
        style = MaterialTheme.typography.titleMedium,
      )
    },
    text = {
      Box(
        modifier = Modifier.fillMaxWidth(),
      ) {
        when {
          isLoading -> {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center,
            ) {
              CircularProgressIndicator()
            }
          }

          else -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            ) {
              items(fontList) { fontName ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  RadioButton(
                    selected = selectedFont == fontName,
                    onClick = { selectedFont = fontName },
                  )
                  Text(fontName)
                }
              }
            }
          }
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          onConfirm(FontFamilyPreference.GoogleFont(selectedFont))
        },
        enabled = !isLoading,
      ) {
        Text(text = stringResource(R.string.save_button))
      }
    },
  )
}

private suspend fun fetchGoogleFonts(): List<String> = withContext(Dispatchers.IO) {
  // TODO: API から取得できるようにする
  // https://developers.google.com/fonts/docs/developer_api
  listOf(
    "IBM Plex Sans",
    "Roboto",
    "Open Sans",
    "Lato",
    "Montserrat",
    "Oswald",
    "Source Sans Pro",
    "Raleway",
    "PT Sans",
    "Noto Sans",
    "Poppins",
    "Ubuntu",
    "Playfair Display",
    "Merriweather",
    "Nunito",
    "Rubik",
    "Work Sans",
    "Inter",
    "Fira Sans",
    "Bebas Neue",
  ).sorted()
}
