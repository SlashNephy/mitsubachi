package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
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
  val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()

  PullToRefreshBox(
    modifier = modifier,
    isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading,
    onRefresh = {
      lazyPagingItems.refresh()
    },
  ) {
    when (val refreshState = lazyPagingItems.loadState.refresh) {
      is LoadState.Loading -> {
        if (lazyPagingItems.itemCount == 0) {
          LoadingScreen()
        }
      }

      is LoadState.Error -> {
        if (lazyPagingItems.itemCount == 0) {
          ErrorScreen(
            refreshState.error as? Exception ?: Exception(refreshState.error),
            onClickRetry = { lazyPagingItems.retry() },
          )
        }
      }

      is LoadState.NotLoading -> {
        // Show content
      }
    }

    if (lazyPagingItems.itemCount > 0 || lazyPagingItems.loadState.refresh is LoadState.Loading) {
      HomeScreenFeedList(
        lazyPagingItems = lazyPagingItems,
        onClickCheckIn = onClickCheckIn,
        onClickLike = { viewModel.likeCheckIn(it) },
        onClickUnlike = { viewModel.unlikeCheckIn(it) },
        formatDateTime = { viewModel.formatAsRelativeTimeSpan(it) },
      )
    }
  }
}

@Composable
private fun HomeScreenFeedList(
  lazyPagingItems: LazyPagingItems<CheckIn>,
  onClickCheckIn: (CheckIn) -> Unit,
  onClickLike: (String) -> Unit,
  onClickUnlike: (String) -> Unit,
  formatDateTime: (java.time.ZonedDateTime) -> String,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      count = lazyPagingItems.itemCount,
      key = lazyPagingItems.itemKey { it.id },
      contentType = lazyPagingItems.itemContentType { "CheckIn" },
    ) { index ->
      val checkIn = lazyPagingItems[index]
      if (checkIn != null) {
        CheckInRow(
          checkIn,
          formatDateTime = formatDateTime,
          onClickCheckIn = { onClickCheckIn(checkIn) },
          onClickLike = { onClickLike(checkIn.id) },
          onClickUnlike = { onClickUnlike(checkIn.id) },
        )

        if (index < lazyPagingItems.itemCount - 1) {
          HorizontalDivider(modifier = Modifier.padding(12.dp))
        }
      }
    }

    // Show loading indicator at the bottom
    when (lazyPagingItems.loadState.append) {
      is LoadState.Loading -> {
        item {
          LoadingMoreIndicator()
        }
      }

      is LoadState.Error -> {
        // Could show error message here
      }

      is LoadState.NotLoading -> {
        // Nothing to show
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
