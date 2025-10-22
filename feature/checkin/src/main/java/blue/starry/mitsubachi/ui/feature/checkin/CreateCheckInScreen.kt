package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.domain.model.Venue

@Composable
fun CreateCheckInScreen(
  venue: Venue,
  onCompleteCheckIn: () -> Unit,
  onCancelCheckIn: () -> Unit,
  viewModel: CreateCheckInScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
    ) {
      CheckInEditor(
        shout = state.value,
        isError = state.hasError,
        onShoutChange = viewModel::onShoutUpdated,
        modifier = Modifier.fillMaxSize(),
      )

      ShoutLengthCounter(
        remaining = state.remainingLength,
        isError = state.hasError,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(horizontal = 24.dp, vertical = 8.dp),
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    ) {
      val keyboardController = LocalSoftwareKeyboardController.current

      CancelButton(
        onClick = {
          keyboardController?.hide()
          onCancelCheckIn()
        },
        modifier = Modifier.weight(1f),
      )

      SubmitButton(
        onClick = {
          viewModel
            .createCheckIn(
              venue = venue,
              shout = state.valueOrNull,
            )
            .invokeOnCompletion {
              onCompleteCheckIn()
            }
        },
        isEnabled = !state.hasError,
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun CheckInEditor(
  shout: String,
  isError: Boolean,
  onShoutChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  // TODO: 写真アップロード対応
  // TODO: ステッカー対応

  TextField(
    value = shout,
    isError = isError,
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

@Composable
private fun ShoutLengthCounter(
  remaining: Int,
  isError: Boolean,
  modifier: Modifier = Modifier,
) {
  val color = if (isError) {
    Color.Red
  } else {
    Color.Gray
  }

  Text(
    remaining.toString(),
    color = color,
    modifier = modifier,
  )
}

@Composable
private fun CancelButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
    onClick = onClick,
    modifier = modifier,
  ) {
    Text("キャンセル")
  }
}

@Composable
private fun SubmitButton(onClick: () -> Unit, isEnabled: Boolean, modifier: Modifier = Modifier) {
  Button(
    onClick = onClick,
    enabled = isEnabled,
    modifier = modifier,
  ) {
    Text("チェックイン!")
  }
}
