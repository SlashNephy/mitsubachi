package blue.starry.mitsubachi.ui.feature.map.search

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold as AdvancedBottomSheetScaffold

private val DEFAULT_LOCATION = LatLng(35.6812, 139.7671) // 東京駅
private const val DEFAULT_ZOOM_LEVEL = 15f

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun SearchMapScreen() {
  val sheetState = rememberBottomSheetState(
    initialValue = SheetValue.PartiallyExpanded,
    defineValues = {
      SheetValue.Hidden at height(64.dp)
      SheetValue.PartiallyExpanded at height(percent = 40)
      SheetValue.Expanded at height(percent = 90)
    },
  )
  val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

  AdvancedBottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetContent = {
      BottomSheetContent(
        modifier = Modifier
          .padding(horizontal = 20.dp)
          .offset(y = (-16).dp),
      )
    },
  ) {
    Content()
  }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun Content(viewModel: SearchMapScreenViewModel = hiltViewModel()) {
  val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

  LaunchedEffect(Unit) {
    if (!locationPermissionState.status.isGranted) {
      locationPermissionState.launchPermissionRequest()
    }
  }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM_LEVEL)
  }

  // カメラ位置が変更されたら、現在地を更新
  LaunchedEffect(cameraPositionState.position) {
    delay(500.milliseconds) // debounce
    viewModel.updateCurrentLocation(cameraPositionState.position.target)
  }

  val state by viewModel.state.collectAsStateWithLifecycle()
  GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
    uiSettings = MapUiSettings(
      mapToolbarEnabled = false,
      myLocationButtonEnabled = false,
      zoomControlsEnabled = false,
    ),
    properties = MapProperties(
      isMyLocationEnabled = locationPermissionState.status.isGranted,
    ),
  ) {
    // ベニューのマーカーを表示
    val recommendations =
      (state as? SearchMapScreenViewModel.UiState.Success)?.venueRecommendations.orEmpty()
    recommendations.forEach { recommendation ->
      val markerState = rememberUpdatedMarkerState(
        position = LatLng(
          recommendation.venue.location.latitude,
          recommendation.venue.location.longitude,
        ),
      )
      Marker(
        state = markerState,
        title = recommendation.venue.name,
        snippet = recommendation.venue.location.address,
        onClick = {
          viewModel.selectVenue(recommendation)
          true
        },
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun BottomSheetContent(
  modifier: Modifier = Modifier,
  viewModel: SearchMapScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val selectedVenue by viewModel.selectedVenue.collectAsStateWithLifecycle()

  Column(
    modifier = modifier.fillMaxWidth(),
  ) {
    // 選択されたベニューがある場合は詳細を表示、ない場合はリストを表示
    val selectedVenue = selectedVenue
    if (selectedVenue != null) {
      // 固定ヘッダー（戻るボタン付き）
      Text(
        text = "← 戻る",
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .padding(bottom = 8.dp)
          .clickable { viewModel.selectVenue(null) },
      )

      // 選択されたベニューの詳細を表示
      VenueRecommendationCard(
        recommendation = selectedVenue,
        modifier = Modifier.padding(vertical = 8.dp),
      )
    } else {
      // 固定ヘッダー
      Text(
        text = "この地域の情報",
        fontWeight = FontWeight.Bold,
      )

      Spacer(modifier = Modifier.padding(vertical = 8.dp))

      // スクロール可能なコンテンツ
      when (val state = state) {
        is SearchMapScreenViewModel.UiState.Loading -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center,
          ) {
            LoadingIndicator()
          }
        }

        is SearchMapScreenViewModel.UiState.Success -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth(),
          ) {
            items(state.venueRecommendations) { recommendation ->
              VenueRecommendationCard(
                recommendation = recommendation,
                modifier = Modifier.padding(vertical = 8.dp),
              )
            }
            item {
              Spacer(modifier = Modifier.height(16.dp))
            }
          }
        }

        is SearchMapScreenViewModel.UiState.Error -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center,
          ) {
            Text(state.message)
          }
        }
      }
    }
  }
}
