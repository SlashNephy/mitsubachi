package blue.starry.mitsubachi.feature.checkin.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.domain.model.CheckIn

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
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(blue.starry.mitsubachi.ui.R.string.back_button),
        )
      }
    },
  )
}
