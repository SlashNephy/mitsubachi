package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun CheckInDetailScreen(
  viewModel: CheckInDetailScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  when (val currentState = state) {
    is CheckInDetailScreenViewModel.UiState.Loading -> {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        LoadingIndicator()
      }
    }

    is CheckInDetailScreenViewModel.UiState.Success -> {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
      ) {
        Text(
          text = currentState.checkIn.venue.name,
          modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "by ${currentState.checkIn.user.displayName}",
          modifier = Modifier.fillMaxWidth(),
        )
        currentState.checkIn.message?.also { message ->
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }

    is CheckInDetailScreenViewModel.UiState.Error -> {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(currentState.message)
      }
    }
  }
}
