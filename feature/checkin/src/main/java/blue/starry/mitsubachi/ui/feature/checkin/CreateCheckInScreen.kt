package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.domain.model.Venue

@Composable
fun CreateCheckInScreen(
  venue: Venue,
  onCompleteCheckIn: () -> Unit,
  onCancelCheckIn: () -> Unit,
  viewModel: CreateCheckInScreenViewModel = hiltViewModel(),
) {
  var shout by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
  ) {
    CheckInEditor(
      shout = shout,
      onShoutChange = { shout = it },
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    ) {
      Button(
        onClick = onCancelCheckIn,
        modifier = Modifier.weight(1f),
      ) {
        Text("キャンセル")
      }

      Button(
        onClick = {
          viewModel
            .createCheckIn(
              venue = venue,
              shout = shout.ifBlank { null },
            )
            .invokeOnCompletion {
              onCompleteCheckIn()
            }
        },
        modifier = Modifier.weight(1f),
      ) {
        Text("チェックイン!")
      }
    }
  }
}

@Composable
private fun CheckInEditor(
  shout: String,
  onShoutChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  // TODO: 写真アップロード対応
  // TODO: ステッカー対応

  TextField(
    value = shout,
    onValueChange = onShoutChange,
    placeholder = {
      Text("何をしていますか？")
    },
    colors = TextFieldDefaults.colors(
      focusedContainerColor = Color.Transparent,
      unfocusedContainerColor = Color.Transparent,
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
    ),
    modifier = modifier,
  )
}
