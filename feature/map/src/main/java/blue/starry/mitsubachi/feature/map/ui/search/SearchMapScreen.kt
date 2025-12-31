package blue.starry.mitsubachi.feature.map.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.ui.compose.permission.AndroidPermission
import blue.starry.mitsubachi.core.ui.compose.permission.PermissionStatus
import blue.starry.mitsubachi.core.ui.compose.permission.rememberPermissionState
import blue.starry.mitsubachi.core.ui.compose.screen.ErrorScreen
import blue.starry.mitsubachi.core.ui.compose.screen.LoadingScreen
import blue.starry.mitsubachi.feature.map.R
import blue.starry.mitsubachi.feature.map.ui.common.Map
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
fun SearchMapScreen(
  modifier: Modifier = Modifier,
  viewModel: SearchMapScreenViewModel = hiltViewModel(),
) {
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
    modifier = modifier,
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

    Box(modifier = Modifier.padding(top = 64.dp)) {
      Column {
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        SearchTextField(
          query = searchQuery,
          onChange = viewModel::updateSearchQuery,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        )

        val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()
        SectionChipRow(
          selectedSection = selectedSection,
          onSelect = viewModel::selectSection,
          contentPadding = PaddingValues(horizontal = 16.dp),
        )
      }
    }
  }
}

@Composable
private fun Content(viewModel: SearchMapScreenViewModel = hiltViewModel()) {
  val permissionState = rememberPermissionState(AndroidPermission.Location)
  LaunchedEffect(Unit) {
    if (permissionState.status != PermissionStatus.Granted) {
      permissionState.launchPermissionRequester()
    }
  }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM_LEVEL)
  }

  // ユーザーの現在地を取得して、カメラを移動
  LaunchedEffect(permissionState.status) {
    if (permissionState.status == PermissionStatus.Granted) {
      viewModel.loadCurrentLocation()
    }
  }

  val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
  LaunchedEffect(currentLocation) {
    currentLocation?.also { location ->
      cameraPositionState.position = CameraPosition.fromLatLngZoom(location, DEFAULT_ZOOM_LEVEL)
      viewModel.updateCurrentLocation(location)
    }
  }

  // カメラ位置が変更されたら、現在地を更新
  LaunchedEffect(cameraPositionState.position) {
    delay(500.milliseconds) // debounce
    viewModel.updateCurrentLocation(cameraPositionState.position.target)
  }

  val state by viewModel.state.collectAsStateWithLifecycle()
  Map(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
    uiSettings = MapUiSettings(
      mapToolbarEnabled = false,
      zoomControlsEnabled = false,
    ),
    properties = MapProperties(
      isMyLocationEnabled = permissionState.status == PermissionStatus.Granted,
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
@Suppress("LongMethod", "CognitiveComplexMethod") // TODO: リファクタリング
@OptIn(ExperimentalLayoutApi::class)
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
        text = stringResource(R.string.search_map_back),
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .clickable(role = Role.Button) { viewModel.selectVenue(null) }
          .padding(bottom = 8.dp),
      )

      // 選択されたベニューの詳細を表示
      VenueRecommendationCard(
        recommendation = selectedVenue,
        modifier = Modifier.padding(vertical = 8.dp),
      )
    } else {
      // 固定ヘッダー
      Text(
        text = stringResource(R.string.search_map_header_area_info),
        fontWeight = FontWeight.Bold,
      )

      Spacer(modifier = Modifier.padding(vertical = 8.dp))

      // スクロール可能なコンテンツ
      when (val state = state) {
        is SearchMapScreenViewModel.UiState.Loading -> {
          LoadingScreen()
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
          ErrorScreen(state.exception, onClickRetry = {
            viewModel.updateCurrentLocation(state.lastLocation)
          })
        }
      }
    }
  }
}
