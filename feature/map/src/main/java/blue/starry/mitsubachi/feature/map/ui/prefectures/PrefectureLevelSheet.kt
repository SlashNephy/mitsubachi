package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletion
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.feature.map.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PrefectureLevelSheet(
  completion: PrefectureCompletion,
  onSelectLevel: (PrefectureLevel) -> Unit,
  onClearOverride: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = completion.prefecture.displayName(),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 8.dp),
      )

      Column(modifier = Modifier.selectableGroup()) {
        PrefectureLevel.entries.forEach { level ->
          val selected = level == completion.effectiveLevel

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .selectable(
                selected = selected,
                onClick = { onSelectLevel(level) },
              )
              .padding(vertical = 8.dp),
          ) {
            RadioButton(selected = selected, onClick = { onSelectLevel(level) })

            LevelColorSwatch(level = level)

            Text(level.displayName(), modifier = Modifier.weight(1f))

            Text(
              text = stringResource(R.string.prefecture_completion_level_points, level.score),
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        }
      }

      if (completion.manualLevel != null) {
        TextButton(
          onClick = onClearOverride,
          modifier = Modifier.padding(bottom = 8.dp),
        ) {
          Text(stringResource(R.string.prefecture_completion_clear_override))
        }
      }
    }
  }
}

@Composable
private fun LevelColorSwatch(level: PrefectureLevel, modifier: Modifier = Modifier) {
  val color = MaterialTheme.colorScheme.prefectureLevelColor(level)

  Row(
    modifier = modifier
      .size(20.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(color),
  ) {}
}
