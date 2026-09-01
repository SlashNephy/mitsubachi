package blue.starry.mitsubachi.feature.map.ui.prefectures

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletion
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import blue.starry.mitsubachi.feature.map.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableMap
import timber.log.Timber

@Composable
fun PrefectureCompletionScreen(
  modifier: Modifier = Modifier,
  viewModel: PrefectureCompletionScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var selectedPrefecture by remember { mutableStateOf<Prefecture?>(null) }

  PullToRefreshBox(
    modifier = modifier,
    isRefreshing = (state as? PrefectureCompletionScreenViewModel.UiState.Success)?.isRefreshing == true,
    onRefresh = { viewModel.refresh() },
  ) {
    when (val state = state) {
      is PrefectureCompletionScreenViewModel.UiState.Loading -> {
        LoadingScreen()
      }

      is PrefectureCompletionScreenViewModel.UiState.Success -> {
        PrefectureCompletionContent(
          summary = state.summary,
          boundaries = state.boundaries,
          selectedPrefecture = selectedPrefecture,
          onSelectPrefecture = { selectedPrefecture = it },
        )
      }

      is PrefectureCompletionScreenViewModel.UiState.Error -> {
        ErrorScreen(state.exception, onClickRetry = viewModel::refresh)
      }
    }
  }

  val successState = state as? PrefectureCompletionScreenViewModel.UiState.Success
  val selectedCompletion = successState?.summary?.completions
    ?.firstOrNull { it.prefecture == selectedPrefecture }

  if (selectedCompletion != null) {
    PrefectureLevelSheet(
      completion = selectedCompletion,
      onSelectLevel = { level -> viewModel.setLevel(selectedCompletion.prefecture, level) },
      onClearOverride = { viewModel.clearLevel(selectedCompletion.prefecture) },
      onDismiss = { selectedPrefecture = null },
    )
  }
}

@Composable
private fun PrefectureCompletionContent(
  summary: PrefectureCompletionSummary,
  boundaries: ImmutableList<PrefectureBoundary>,
  selectedPrefecture: Prefecture?,
  onSelectPrefecture: (Prefecture) -> Unit,
  modifier: Modifier = Modifier,
) {
  val levels = remember(summary) {
    summary.completions.associate { it.prefecture to it.effectiveLevel }.toImmutableMap()
  }
  val groupedByLevel = remember(summary) {
    summary.completions.groupBy { it.effectiveLevel }
  }

  LazyColumn(modifier = modifier.fillMaxWidth()) {
    item(key = "score-header") {
      ScoreHeader(summary)
    }

    item(key = "map") {
      PrefectureMap(
        boundaries = boundaries,
        levels = levels,
        selected = selectedPrefecture,
        onSelect = onSelectPrefecture,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
      )
    }

    item(key = "legend") {
      LegendSection()
    }

    for (level in PrefectureLevel.entries.sortedDescending()) {
      val group = groupedByLevel[level].orEmpty()
      if (group.isEmpty()) {
        continue
      }

      item(key = "group-header-${level.name}") {
        PrefectureGroupHeader(level = level, count = group.size)
      }

      items(group, key = { "prefecture-${it.prefecture.name}" }) { completion ->
        PrefectureListRow(
          completion = completion,
          onClick = { onSelectPrefecture(completion.prefecture) },
        )
      }
    }

    item(key = "credit") {
      CreditFooter()
    }
  }
}

@Composable
private fun ScoreHeader(summary: PrefectureCompletionSummary, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
  ) {
    Text(
      text = stringResource(R.string.prefecture_completion_score, summary.totalScore, summary.maxScore),
      style = MaterialTheme.typography.displaySmall,
    )

    if (summary.visitedCountryCodes.isNotEmpty()) {
      Text(
        text = pluralStringResource(
          R.plurals.prefecture_completion_countries,
          summary.visitedCountryCodes.size,
          summary.visitedCountryCodes.size,
        ),
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

@Composable
private fun LegendSection(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
  ) {
    Text(
      text = stringResource(R.string.prefecture_completion_legend),
      style = MaterialTheme.typography.titleMedium,
    )

    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(top = 8.dp),
    ) {
      for (level in PrefectureLevel.entries) {
        LegendItem(level)
      }
    }
  }
}

@Composable
private fun LegendItem(level: PrefectureLevel, modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    modifier = modifier,
  ) {
    ColorSwatch(level = level)

    Text(level.displayName(), style = MaterialTheme.typography.bodySmall)

    Text(
      text = stringResource(R.string.prefecture_completion_level_points, level.score),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun ColorSwatch(level: PrefectureLevel, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .size(16.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(MaterialTheme.colorScheme.prefectureLevelColor(level)),
  ) {}
}

@Composable
private fun PrefectureGroupHeader(level: PrefectureLevel, count: Int, modifier: Modifier = Modifier) {
  Text(
    text = "${level.displayName()} ($count)",
    style = MaterialTheme.typography.titleSmall,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Composable
private fun PrefectureListRow(
  completion: PrefectureCompletion,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(completion.prefecture.displayName(), style = MaterialTheme.typography.bodyLarge)
      Text(
        text = pluralStringResource(
          R.plurals.prefecture_completion_venue_count,
          completion.venueCount,
          completion.venueCount,
        ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    if (completion.manualLevel != null) {
      AssistChip(
        onClick = onClick,
        label = { Text(stringResource(R.string.prefecture_completion_overridden)) },
      )
    }
  }
}

@Composable
private fun CreditFooter(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  Text(
    text = stringResource(R.string.prefecture_completion_credit),
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        try {
          context.startActivity(Intent(Intent.ACTION_VIEW, "https://uub.jp/".toUri()))
        } catch (e: ActivityNotFoundException) {
          Timber.w(e, "No activity found to handle the credit link")
        }
      }
      .padding(16.dp),
  )
}

@Composable
internal fun Prefecture.displayName(): String {
  // 日本語ロケールでは漢字表記、それ以外はローマ字表記にする
  val locale = LocalConfiguration.current.locales[0]
  return if (locale.language == "ja") japaneseName else romajiName.replaceFirstChar { it.uppercase() }
}

@Composable
internal fun PrefectureLevel.displayName(): String {
  return stringResource(
    when (this) {
      PrefectureLevel.Unvisited -> R.string.prefecture_completion_level_unvisited
      PrefectureLevel.PassedThrough -> R.string.prefecture_completion_level_passed_through
      PrefectureLevel.Landed -> R.string.prefecture_completion_level_landed
      PrefectureLevel.Visited -> R.string.prefecture_completion_level_visited
      PrefectureLevel.Stayed -> R.string.prefecture_completion_level_stayed
      PrefectureLevel.Lived -> R.string.prefecture_completion_level_lived
    },
  )
}
