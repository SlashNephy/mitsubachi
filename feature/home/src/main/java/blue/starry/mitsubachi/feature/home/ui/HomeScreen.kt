package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.ui.compose.foundation.CheckInRow
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.time.ZonedDateTime

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
        HomeScreenFeedList(
          state = state,
          onClickCheckIn = onClickCheckIn,
          onClickLike = { viewModel.likeCheckIn(it) },
          onClickUnlike = { viewModel.unlikeCheckIn(it) },
          onLoadMore = { viewModel.loadMore() },
          formatDateTime = { viewModel.formatAsRelativeTimeSpan(it) },
        )
      }

      is HomeScreenViewModel.UiState.Error -> {
        ErrorScreen(state.exception, onClickRetry = viewModel::refresh)
      }
    }
  }
}

@Composable
private fun HomeScreenFeedList(
  state: HomeScreenViewModel.UiState.Success,
  onClickCheckIn: (CheckIn) -> Unit,
  onClickLike: (String) -> Unit,
  onClickUnlike: (String) -> Unit,
  onLoadMore: () -> Unit,
  formatDateTime: (ZonedDateTime) -> String,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  LaunchedEffect(listState, state.feed.size) {
    snapshotFlow {
      val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
      lastVisibleItem?.index == state.feed.lastIndex
    }
      .distinctUntilChanged()
      .filter { it }
      .collect {
        if (state.hasMore && !state.isLoadingMore) {
          currentOnLoadMore()
        }
      }
  }

  LazyColumn(
    state = listState,
    modifier = modifier.fillMaxSize(),
  ) {
    itemsIndexed(state.feed, key = { _, checkIn -> checkIn.id }) { index, checkIn ->
      CheckInRow(
        checkIn,
        formatDateTime = formatDateTime,
        onClickCheckIn = { onClickCheckIn(checkIn) },
        onClickLike = { onClickLike(checkIn.id) },
        onClickUnlike = { onClickUnlike(checkIn.id) },
      )

      if (index < state.feed.lastIndex) {
        HorizontalDivider(modifier = Modifier.padding(12.dp))
      }
    }

    if (state.isLoadingMore) {
      item {
        LoadingMoreIndicator()
      }
    }
  }
}

@Composable
private fun LoadingMoreIndicator(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}
