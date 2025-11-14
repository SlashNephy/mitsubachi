package blue.starry.mitsubachi.feature.map.ui.histories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.ui.compose.permission.AndroidPermission
import blue.starry.mitsubachi.core.ui.compose.permission.PermissionStatus
import blue.starry.mitsubachi.core.ui.compose.permission.rememberPermissionState
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import blue.starry.mitsubachi.feature.map.ui.common.Map
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

internal const val DEFAULT_ZOOM_LEVEL = 5f

// TODO: 可変にする。クラスタリングを有効にできるようにする (6f くらいで)。
internal const val DEFAULT_CLUSTERING_THRESHOLD_ZOOM_LEVEL = 0f // デフォルト無効

@Composable
@OptIn(MapsComposeExperimentalApi::class)
@Suppress("LongMethod") // TODO: リファクタリング
fun VenueHistoriesScreen(viewModel: VenueHistoriesScreenViewModel = hiltViewModel()) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  val permissionState = rememberPermissionState(AndroidPermission.Location)
  LaunchedEffect(Unit) {
    if (permissionState.status != PermissionStatus.Granted) {
      permissionState.launchPermissionRequester()
    }
  }

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
        val coroutineScope = rememberCoroutineScope()
        val cameraPositionState = rememberCameraPositionState {
          if (state.data.isNotEmpty()) {
            position = CameraPosition.fromLatLngZoom(
              state.initialPosition,
              DEFAULT_ZOOM_LEVEL,
            )
          }
        }

        Box(modifier = Modifier.fillMaxSize()) {
          Map(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
              mapToolbarEnabled = false,
              zoomControlsEnabled = false,
              myLocationButtonEnabled = false,
            ),
            properties = MapProperties(
              isMyLocationEnabled = permissionState.status == PermissionStatus.Granted,
            ),
          ) {
            VenueHistoriesMapView(state.data)
          }

          // FAB ボタン群
          Column(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            // 現在地へ移動ボタン
            if (permissionState.status == PermissionStatus.Granted) {
              SmallFloatingActionButton(
                onClick = {
                  coroutineScope.launch {
                    viewModel.findCurrentLocation()?.also {
                      cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                          it,
                          15f,
                        ),
                      )
                    }
                  }
                },
              ) {
                Icon(
                  imageVector = Icons.Filled.MyLocation,
                  contentDescription = "現在地へ移動",
                )
              }
            }

            // ズームインボタン
            SmallFloatingActionButton(
              onClick = {
                coroutineScope.launch {
                  cameraPositionState.animate(CameraUpdateFactory.zoomIn())
                }
              },
            ) {
              Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "ズームイン",
              )
            }

            // ズームアウトボタン
            SmallFloatingActionButton(
              onClick = {
                coroutineScope.launch {
                  cameraPositionState.animate(CameraUpdateFactory.zoomOut())
                }
              },
            ) {
              Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "ズームアウト",
              )
            }
          }
        }
      }

      is VenueHistoriesScreenViewModel.UiState.Error -> {
        ErrorScreen(state.exception, viewModel::refresh)
      }
    }
  }
}
