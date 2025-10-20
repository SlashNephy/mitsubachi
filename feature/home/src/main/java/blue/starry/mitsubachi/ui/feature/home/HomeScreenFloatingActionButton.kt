package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun HomeScreenFloatingActionButton(onClickCheckInButton: () -> Unit) {
  ExtendedFloatingActionButton(
    icon = {
      Icon(
        imageVector = Icons.Default.Place,
        contentDescription = stringResource(R.string.check_in_button),
      )
    },
    text = {
      Text(stringResource(R.string.check_in_button))
    },
    onClick = onClickCheckInButton,
  )
}
