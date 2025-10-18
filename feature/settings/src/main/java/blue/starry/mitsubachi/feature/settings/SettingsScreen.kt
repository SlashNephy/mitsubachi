package blue.starry.mitsubachi.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SettingsScreen(
  onSignOut: () -> Unit,
  viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
  Row(horizontalArrangement = Arrangement.Center) {
    Button(
      onClick = {
        viewModel
          .signOut()
          .invokeOnCompletion {
            onSignOut()
          }
      },
      colors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
      ),
    ) {
      Text("ログアウト")
    }
  }
}
