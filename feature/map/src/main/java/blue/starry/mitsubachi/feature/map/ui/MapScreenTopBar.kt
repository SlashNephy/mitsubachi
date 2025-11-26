package blue.starry.mitsubachi.feature.map.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import androidx.compose.ui.res.painterResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MapScreenTopBar(onBack: () -> Unit) {
  TopAppBar(
    title = {},
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          painter = painterResource(MaterialSymbols.arrow_back),
          contentDescription = stringResource(blue.starry.mitsubachi.core.ui.compose.R.string.back_button),
        )
      }
    },
  )
}
