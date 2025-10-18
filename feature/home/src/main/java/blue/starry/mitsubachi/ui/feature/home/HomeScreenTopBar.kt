package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenTopBar(onClickSettingsButton: () -> Unit) {
  TopAppBar(
    title = {},
    navigationIcon = {
      // TODO: アバターメニュー
    },
    actions = {
      val state = rememberTooltipState()

      TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
          TooltipAnchorPosition.Above,
        ),
        tooltip = {
          PlainTooltip {
            Text(stringResource(R.string.settings_button))
          }
        },
        state = state,
      ) {
        IconButton(onClick = onClickSettingsButton) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings_button),
          )
        }
      }
    },
  )
}
