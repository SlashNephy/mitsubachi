package blue.starry.mitsubachi.ui.feature.map.histories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.ui.screen.ErrorScreen
import blue.starry.mitsubachi.ui.screen.LoadingScreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState

internal const val DEFAULT_ZOOM_LEVEL = 5f

// TODO: 可変にする。クラスタリングを有効にできるようにする (6f くらいで)。
internal const val DEFAULT_CLUSTERING_THRESHOLD_ZOOM_LEVEL = 0f // デフォルト無効

@Composable
@OptIn(MapsComposeExperimentalApi::class)
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
        LoadingScreen()
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
        ErrorScreen(state.exception)
      }
    }
  }
}
