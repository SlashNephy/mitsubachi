package blue.starry.mitsubachi.ui.feature.checkin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          itemsIndexed(
            items = state.venues,
            key = { _, venue -> venue.id },
          ) { index, venue ->
            VenueRow(venue, onClickVenue = onClickVenue)

            if (index < state.venues.lastIndex) {
              HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(12.dp))
            }
          }
        }
      }

      is NearbyVenuesScreenViewModel.UiState.Error -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(state.message, modifier = Modifier.padding(16.dp))
        }
      }
    }
  }
}
