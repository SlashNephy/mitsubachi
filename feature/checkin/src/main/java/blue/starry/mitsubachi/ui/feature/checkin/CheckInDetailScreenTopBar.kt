package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CheckInDetailScreenTopBar(
  onBack: () -> Unit,
) {
  TopAppBar(
    title = { Text(stringResource(R.string.checkin_detail_title)) },
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(R.string.back_button),
        )
      }
    },
  )
}
