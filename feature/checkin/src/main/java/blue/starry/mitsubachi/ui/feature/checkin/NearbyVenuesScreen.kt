package blue.starry.mitsubachi.ui.feature.checkin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.domain.model.Venue

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun NearbyVenuesScreen(
  onClickVenue: (Venue) -> Unit,
  viewModel: NearbyVenuesScreenViewModel = hiltViewModel(),
  topBarViewModel: NearbyVenuesScreenTopBarViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

  val query by topBarViewModel.query.collectAsStateWithLifecycle(null)
  LaunchedEffect(query) {
    if (query != null) {
      viewModel.refresh(query)
    }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = viewModel::onPermissionResult,
  )

  PullToRefreshBox(
    isRefreshing = (state as? NearbyVenuesScreenViewModel.UiState.Success)?.isRefreshing == true,
    onRefresh = {
      viewModel.refresh()
    },
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
  ) {
    when (val state = state) {
      is NearbyVenuesScreenViewModel.UiState.PermissionRequesting -> {
        LaunchedEffect(state.anyOf) {
          permissionLauncher.launch(state.anyOf.toTypedArray())
        }
      }

      is NearbyVenuesScreenViewModel.UiState.PermissionRequestDenied -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(stringResource(R.string.location_permission_missing))
        }
      }

      is NearbyVenuesScreenViewModel.UiState.Loading -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          LoadingIndicator()
        }
      }

      is NearbyVenuesScreenViewModel.UiState.Success -> {
        VenueListContent(
          state = state,
          sortOrder = sortOrder,
          onSortOrderChange = viewModel::onUpdateSortOrder,
          onClickVenue = onClickVenue,
        )
      }

      is NearbyVenuesScreenViewModel.UiState.Error -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(state.message, modifier = Modifier.padding(16.dp))
        }
      }
    }
  }
}

@Composable
private fun VenueListContent(
  state: NearbyVenuesScreenViewModel.UiState.Success,
  sortOrder: NearbyVenuesSortOrder,
  onSortOrderChange: (NearbyVenuesSortOrder) -> Unit,
  onClickVenue: (Venue) -> Unit,
) {
  if (state.venues.isEmpty()) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
      Text(
        text = stringResource(R.string.no_venues_found),
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center,
      )
    }
  } else {
    Column(modifier = Modifier.fillMaxSize()) {
      // Sort buttons
      SortButtons(
        sortOrder = sortOrder,
        onSortOrderChange = onSortOrderChange,
      )

      // Venue list
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(
          items = state.sortedVenues,
          key = { _, venue -> venue.id },
        ) { index, venue ->
          VenueRow(venue, onClickVenue = onClickVenue)

          if (index < state.venues.lastIndex) {
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(12.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun SortButtons(
  sortOrder: NearbyVenuesSortOrder,
  onSortOrderChange: (NearbyVenuesSortOrder) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    FilterChip(
      selected = sortOrder == NearbyVenuesSortOrder.RELEVANCE,
      onClick = { onSortOrderChange(NearbyVenuesSortOrder.RELEVANCE) },
      label = { Text(stringResource(R.string.sort_by_relevance)) },
      leadingIcon = {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.TrendingUp,
          contentDescription = null,
        )
      },
    )
    FilterChip(
      selected = sortOrder == NearbyVenuesSortOrder.DISTANCE,
      onClick = { onSortOrderChange(NearbyVenuesSortOrder.DISTANCE) },
      label = { Text(stringResource(R.string.sort_by_distance)) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Place,
          contentDescription = null,
        )
      },
    )
  }
}
