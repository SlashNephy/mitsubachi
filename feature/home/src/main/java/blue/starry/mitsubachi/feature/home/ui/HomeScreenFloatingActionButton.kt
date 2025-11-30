package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.home.R

@Composable
fun HomeScreenFloatingActionButton(onClickCheckInButton: () -> Unit) {
  ExtendedFloatingActionButton(
    icon = {
      Icon(
        painter = painterResource(MaterialSymbols.add_location_alt_filled),
        contentDescription = stringResource(R.string.check_in_button),
      )
    },
    text = {
      Text(stringResource(R.string.check_in_button))
    },
    onClick = onClickCheckInButton,
  )
}
