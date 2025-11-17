package blue.starry.mitsubachi.feature.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.ui.compose.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreenTopBar(onBack: () -> Unit) {
  TopAppBar(
    title = {},
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Default.ArrowBack,
          contentDescription = stringResource(R.string.back_button),
        )
      }
    },
  )
}
