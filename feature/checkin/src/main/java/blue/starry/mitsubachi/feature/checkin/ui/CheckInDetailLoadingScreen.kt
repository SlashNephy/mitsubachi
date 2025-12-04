package blue.starry.mitsubachi.feature.checkin.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen

@Composable
fun CheckInDetailLoadingScreen(
  id: String,
  onLoadCheckIn: (CheckIn) -> Unit,
  viewModel: CheckInDetailLoadingScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(id) {
    viewModel.fetchCheckIn(id)
  }

  when (val state = state) {
    is CheckInDetailLoadingScreenViewModel.UiState.Loading -> {
      LoadingScreen()
    }

    is CheckInDetailLoadingScreenViewModel.UiState.Loaded -> {
      LaunchedEffect(onLoadCheckIn, state) {
        onLoadCheckIn(state.checkIn)
      }
    }

    is CheckInDetailLoadingScreenViewModel.UiState.Error -> {
      ErrorScreen(state.exception, onClickRetry = { viewModel.fetchCheckIn(id) })
    }
  }
}
