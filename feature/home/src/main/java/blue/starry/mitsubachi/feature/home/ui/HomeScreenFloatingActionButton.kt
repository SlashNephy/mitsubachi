package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.feature.home.R
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import androidx.compose.ui.res.painterResource

@Composable
fun HomeScreenFloatingActionButton(onClickCheckInButton: () -> Unit) {
  ExtendedFloatingActionButton(
    icon = {
      Icon(
        painter = painterResource(MaterialSymbols.place),
        contentDescription = stringResource(R.string.check_in_button),
      )
    },
    text = {
      Text(stringResource(R.string.check_in_button))
    },
    onClick = onClickCheckInButton,
  )
}
