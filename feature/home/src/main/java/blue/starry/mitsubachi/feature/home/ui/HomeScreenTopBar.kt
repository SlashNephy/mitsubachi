package blue.starry.mitsubachi.feature.home.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.home.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenTopBar(
  onClickSettingsButton: () -> Unit,
  modifier: Modifier = Modifier,
) {
  TopAppBar(
    modifier = modifier,
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
            painter = painterResource(MaterialSymbols.settings),
            contentDescription = stringResource(R.string.settings_button),
          )
        }
      }
    },
  )
}
