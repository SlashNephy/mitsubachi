package blue.starry.mitsubachi.ui.feature.map.histories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState

internal const val DEFAULT_ZOOM_LEVEL = 5f

// TODO: 可変にする。クラスタリングを有効にできるようにする (6f くらいで)。
internal const val DEFAULT_CLUSTERING_THRESHOLD_ZOOM_LEVEL = 0f // デフォルト無効

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, MapsComposeExperimentalApi::class)
fun VenueHistoriesScreen(viewModel: VenueHistoriesScreenViewModel = hiltViewModel()) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  // FIXME: Pull to Refresh のジェスチャが Map に吸われてしまって発動していない
  PullToRefreshBox(
    isRefreshing = (state as? VenueHistoriesScreenViewModel.UiState.Success)?.isRefreshing == true,
    onRefresh = {
      viewModel.refresh()
    },
  ) {
    when (val state = state) {
      is VenueHistoriesScreenViewModel.UiState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          LoadingIndicator()
        }
      }

      is VenueHistoriesScreenViewModel.UiState.Success -> {
        val cameraPositionState = rememberCameraPositionState {
          if (state.data.isNotEmpty()) {
            position = CameraPosition.fromLatLngZoom(
              state.weightedAveragePosition,
              DEFAULT_ZOOM_LEVEL,
            )
          }
        }

        GoogleMap(
          modifier = Modifier.fillMaxSize(),
          cameraPositionState = cameraPositionState,
          uiSettings = MapUiSettings(mapToolbarEnabled = false),
        ) {
          VenueHistoriesMapView(state.data)
        }
      }

      is VenueHistoriesScreenViewModel.UiState.Error -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          Text(state.message, modifier = Modifier.padding(16.dp))
        }
      }
    }
  }
}
