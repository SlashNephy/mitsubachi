package blue.starry.mitsubachi.feature.checkin.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.checkin.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CheckInDetailScreenTopBar(
  checkIn: CheckIn,
  onBack: () -> Unit,
) {
  TopAppBar(
    title = {
      checkIn.user?.also {
        Text(stringResource(R.string.check_in_detail_top_bar_title).format(it.displayName))
      }
    },
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          painterResource(MaterialSymbols.arrow_back),
          contentDescription = stringResource(blue.starry.mitsubachi.core.ui.compose.R.string.back_button),
        )
      }
    },
  )
}
