package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.ColorSchemePreference
import blue.starry.mitsubachi.core.domain.model.FontFamilyPreference
import blue.starry.mitsubachi.core.domain.model.GoogleWebFont
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import blue.starry.mitsubachi.core.ui.compose.setting.SettingItem
import blue.starry.mitsubachi.core.ui.compose.setting.SettingSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.settings.R

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
        Text(
          text = when (val font = applicationSettings.fontFamilyPreference) {
            is FontFamilyPreference.Default -> "デフォルト"
            is FontFamilyPreference.GoogleFont -> font.fontName
          },
        )
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

@Suppress("LongMethod", "CognitiveComplexMethod")
@Composable
internal fun FontFamilyDialog(
  currentPreference: FontFamilyPreference,
  onConfirm: (FontFamilyPreference) -> Unit,
  onDismiss: () -> Unit,
  viewModel: FontFamilyDialogViewModel = hiltViewModel(),
) {
  var selectedFont by remember { mutableStateOf(currentPreference) }
  val state by viewModel.state.collectAsStateWithLifecycle()

  val keywordState = rememberTextFieldState()
  val categories = rememberSaveable {
    mutableStateSetOf(*FontFamilyDialogViewModel.availableCategories)
  }
  val subsets = rememberSaveable {
    mutableStateSetOf(*FontFamilyDialogViewModel.availableSubsets)
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
      when (val state = state) {
        is FontFamilyDialogViewModel.UiState.Loading -> {
          LoadingScreen(indicatorScale = 1.0f)
        }

        is FontFamilyDialogViewModel.UiState.Loaded -> {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
              state = keywordState,
              placeholder = {
                Text("フォント名...")
              },
              modifier = Modifier.fillMaxWidth(),
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(
                FontFamilyDialogViewModel.availableCategories,
                key = { it.ordinal },
              ) { category ->
                FilterChip(
                  selected = categories.contains(category),
                  onClick = { categories.toggle(category) },
                  label = {
                    Text(category.name)
                  },
                )
              }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(
                FontFamilyDialogViewModel.availableSubsets,
                key = { it.hashCode() },
              ) { subset ->
                FilterChip(
                  selected = subsets.contains(subset),
                  onClick = { subsets.toggle(subset) },
                  label = {
                    Text(
                      text = when (subset) {
                        GoogleWebFont.Subset.Latin -> "ラテン"
                        GoogleWebFont.Subset.Japanese -> "日本語"
                        GoogleWebFont.Subset.Korean -> "韓国語"
                        is GoogleWebFont.Subset.Other -> subset.name
                      },
                    )
                  },
                  leadingIcon = {
                    Icon(
                      painter = painterResource(
                        id = when (subset) {
                          GoogleWebFont.Subset.Latin -> MaterialSymbols.abc
                          GoogleWebFont.Subset.Japanese -> MaterialSymbols.language_japanese_kana
                          GoogleWebFont.Subset.Korean -> MaterialSymbols.language_korean_latin
                          is GoogleWebFont.Subset.Other -> MaterialSymbols.language
                        },
                      ),
                      contentDescription = null,
                    )
                  },
                )
              }
            }

            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .selectableGroup(),
            ) {
              items(
                items = state.fonts.asSequence()
                  .filter { font ->
                    font.family.contains(
                      keywordState.text,
                      ignoreCase = true,
                    )
                  }
                  .filter { font ->
                    categories.contains(font.category)
                  }
                  .filter { font ->
                    font.subsets.any { subset ->
                      subsets.contains(subset)
                    }
                  }
                  .toList(),
                key = { it.family },
              ) { font ->
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      selectedFont = FontFamilyPreference.GoogleFont(font.family)
                    },
                ) {
                  RadioButton(
                    selected = when (val selectedFont = selectedFont) {
                      is FontFamilyPreference.Default -> false
                      is FontFamilyPreference.GoogleFont -> selectedFont.fontName == font.family
                    },
                    onClick = {
                      selectedFont = FontFamilyPreference.GoogleFont(font.family)
                    },
                  )
                  Text(font.family)
                }
              }
            }
          }
        }

        is FontFamilyDialogViewModel.UiState.Error -> {
          ErrorScreen(exception = state.exception, onClickRetry = viewModel::refresh)
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel_button))
      }
    },
    confirmButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
          onClick = {
            onConfirm(FontFamilyPreference.Default)
          },
        ) {
          Text(text = "デフォルトに戻す")
        }

        TextButton(
          onClick = {
            onConfirm(selectedFont)
          },
          enabled = state is FontFamilyDialogViewModel.UiState.Loaded,
        ) {
          Text(text = stringResource(R.string.save_button))
        }
      }
    },
  )
}

private fun <E> MutableSet<E>.toggle(element: E) {
  if (contains(element)) {
    remove(element)
  } else {
    add(element)
  }
}
