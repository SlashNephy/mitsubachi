package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun HomeScreen(
  onClickVenue: (latitude: Double, longitude: Double, title: String?) -> Unit,
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
        Box(
          modifier = Modifier
            .fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          LoadingIndicator()
        }
      }

      is HomeScreenViewModel.UiState.Success -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize(),
        ) {
          itemsIndexed(state.feed, key = { _, checkIn -> checkIn.id }) { index, checkIn ->
            CheckInRow(checkIn, onClickVenue = onClickVenue)

            if (index < state.feed.lastIndex) {
              HorizontalDivider(modifier = Modifier.padding(12.dp))
            }
          }
        }
      }

      is HomeScreenViewModel.UiState.Error -> {
        Box(
          modifier = Modifier
            .fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          Text(state.message, modifier = Modifier.padding(16.dp))
        }
      }
    }
  }
}
