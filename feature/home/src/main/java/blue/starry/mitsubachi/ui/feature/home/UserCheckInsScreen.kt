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
import blue.starry.mitsubachi.domain.model.CheckIn

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun UserCheckInsScreen(
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
  viewModel: UserCheckInsScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  PullToRefreshBox(
    isRefreshing = (state as? UserCheckInsScreenViewModel.UiState.Success)?.isRefreshing == true,
    onRefresh = {
      viewModel.refresh()
    },
  ) {
    when (val state = state) {
      is UserCheckInsScreenViewModel.UiState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          LoadingIndicator()
        }
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
