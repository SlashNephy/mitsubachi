package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.domain.model.CheckIn
import blue.starry.mitsubachi.ui.screen.ErrorScreen
import blue.starry.mitsubachi.ui.screen.LoadingScreen

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun HomeScreen(
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
  viewModel: HomeScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  PullToRefreshBox(
    isRefreshing = (state as? HomeScreenViewModel.UiState.Success)?.isRefreshing == true,
    onRefresh = {
      viewModel.refresh()
    },
  ) {
    when (val state = state) {
      is HomeScreenViewModel.UiState.Loading -> {
        LoadingScreen()
      }

      is HomeScreenViewModel.UiState.Success -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize(),
        ) {
          itemsIndexed(state.feed, key = { _, checkIn -> checkIn.id }) { index, checkIn ->
            CheckInRow(
              checkIn,
              onClickCheckIn = onClickCheckIn,
            )

            if (index < state.feed.lastIndex) {
              HorizontalDivider(modifier = Modifier.padding(12.dp))
            }
          }
        }
      }

      is HomeScreenViewModel.UiState.Error -> {
        ErrorScreen(state.exception, viewModel::refresh)
      }
    }
  }
}
