package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreenTopBar(
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  TopAppBar(
    modifier = modifier,
    title = {},
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          painter = painterResource(MaterialSymbols.arrow_back),
          contentDescription = stringResource(R.string.back_button),
        )
      }
    },
  )
}
