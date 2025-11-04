package blue.starry.mitsubachi.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
  onSignOut: () -> Unit,
  viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
  val crashlyticsEnabled by viewModel.isFirebaseCrashlyticsEnabled.collectAsStateWithLifecycle()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
  ) {
    // Crashlytics Setting
    ListItem(
      headlineContent = { Text("Firebase Crashlytics") },
      supportingContent = { Text("クラッシュレポートを送信する") },
      trailingContent = {
        Switch(
          checked = crashlyticsEnabled,
          onCheckedChange = { viewModel.setFirebaseCrashlyticsEnabled(it) },
        )
      },
    )

    HorizontalDivider()

    // Sign Out Button
    ListItem(
      headlineContent = {
        Button(
          onClick = {
            viewModel
              .signOut()
              .invokeOnCompletion {
                onSignOut()
              }
          },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
          ),
        ) {
          Text("ログアウト")
        }
      },
      modifier = Modifier.padding(vertical = 8.dp),
    )
  }
}
