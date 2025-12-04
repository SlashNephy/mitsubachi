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
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.ui.compose.foundation.CheckInRow
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun HomeScreen(
  onClickCheckIn: (CheckIn) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: HomeScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  PullToRefreshBox(
    modifier = modifier,
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
              formatDateTime = { viewModel.formatAsRelativeTimeSpan(it) },
              onClickCheckIn = { onClickCheckIn(checkIn) },
              onClickLike = { viewModel.likeCheckIn(checkIn.id) },
              onClickUnlike = { viewModel.unlikeCheckIn(checkIn.id) },
            )

            if (index < state.feed.lastIndex) {
              HorizontalDivider(modifier = Modifier.padding(12.dp))
            }
          }
        }
      }

      is HomeScreenViewModel.UiState.Error -> {
        ErrorScreen(state.exception, onClickRetry = viewModel::refresh)
      }
    }
  }
}
