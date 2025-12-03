package blue.starry.mitsubachi.feature.checkin.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.checkin.R

@Composable
@Suppress("LongMethod") // TODO: 後でなんとかする
fun CreateCheckInScreen(
  venue: Venue,
  onCompleteCheckIn: () -> Unit,
  onCancelCheckIn: () -> Unit,
  viewModel: CreateCheckInScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  // TODO: ステート管理を ViewModel に移す
  val imageUris = remember { mutableStateListOf<Uri>() }
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia(),
  ) { uris ->
    imageUris.clear()
    imageUris.addAll(uris)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
  ) {
    Row(modifier = Modifier.height(64.dp)) {
      BadgedBox(
        badge = {
          if (imageUris.isNotEmpty()) {
            Badge {
              Text(imageUris.size.toString())
            }
          }
        },
      ) {
        IconButton(
          onClick = {
            launcher.launch(
              PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.SingleMimeType("image/jpeg"),
                maxItems = 4,
                isOrderedSelection = true,
              ),
            )
          },
        ) {
          Icon(painterResource(MaterialSymbols.photo_camera), contentDescription = null)
        }
      }

      // TODO: ステッカー対応
    }

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
              isPublic = true, // TODO: 非公開対応
              imageUris = imageUris,
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
  TextField(
    value = shout,
    isError = isError,
    onValueChange = onShoutChange,
    placeholder = {
      Text(stringResource(R.string.what_are_you_doing_placeholder))
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
    MaterialTheme.colorScheme.error
  } else {
    MaterialTheme.colorScheme.secondary
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
    Text(stringResource(R.string.cancel_button))
  }
}

@Composable
private fun SubmitButton(onClick: () -> Unit, isEnabled: Boolean, modifier: Modifier = Modifier) {
  Button(
    onClick = onClick,
    enabled = isEnabled,
    modifier = modifier,
  ) {
    Text(stringResource(R.string.check_in_submit_button))
  }
}
