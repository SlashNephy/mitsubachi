package blue.starry.mitsubachi.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
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
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen

@Composable
fun UserCheckInsScreen(
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: UserCheckInsScreenViewModel = hiltViewModel(),
) {
  val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()

  PullToRefreshBox(
    modifier = modifier,
    isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading &&
      lazyPagingItems.itemCount > 0,
    onRefresh = {
      lazyPagingItems.refresh()
    },
  ) {
    val refreshState = lazyPagingItems.loadState.refresh
    when {
      refreshState is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
        LoadingScreen()
      }

      refreshState is LoadState.Error && lazyPagingItems.itemCount == 0 -> {
        ErrorScreen(
          refreshState.error as? Exception ?: Exception(refreshState.error),
          onClickRetry = { lazyPagingItems.retry() },
        )
      }

      else -> {
        UserCheckInsList(
          lazyPagingItems = lazyPagingItems,
          onClickCheckIn = onClickCheckIn,
        )
      }
    }
  }
}

@Composable
private fun UserCheckInsList(
  lazyPagingItems: LazyPagingItems<CheckIn>,
  onClickCheckIn: (checkIn: CheckIn) -> Unit,
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
        UserCheckInRow(
          checkIn,
          onClickCheckIn = onClickCheckIn,
        )

        if (index < lazyPagingItems.itemCount - 1) {
          HorizontalDivider(modifier = Modifier.padding(12.dp))
        }
      }
    }

    if (lazyPagingItems.loadState.append is LoadState.Loading) {
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
