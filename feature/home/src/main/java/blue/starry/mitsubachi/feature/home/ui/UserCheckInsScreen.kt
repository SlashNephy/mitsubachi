package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen

@Composable
fun UserCheckInsScreen(
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: UserCheckInsScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  PullToRefreshBox(
    modifier = modifier,
    isRefreshing = (state as? UserCheckInsScreenViewModel.UiState.Success)?.isRefreshing == true,
    onRefresh = {
      viewModel.refresh()
    },
  ) {
    when (val state = state) {
      is UserCheckInsScreenViewModel.UiState.Loading -> {
        LoadingScreen()
      }

      is UserCheckInsScreenViewModel.UiState.Success -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize(),
        ) {
          itemsIndexed(state.checkIns, key = { _, checkIn -> checkIn.id }) { index, checkIn ->
            UserCheckInRow(
              checkIn,
              onClickCheckIn = onClickCheckIn,
            )

            if (index < state.checkIns.lastIndex) {
              HorizontalDivider(modifier = Modifier.padding(12.dp))
            }
          }
        }
      }

      is UserCheckInsScreenViewModel.UiState.Error -> {
        ErrorScreen(state.exception, onClickRetry = viewModel::refresh)
      }
    }
  }
}
