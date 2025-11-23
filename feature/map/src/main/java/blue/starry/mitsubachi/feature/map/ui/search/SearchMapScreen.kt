package blue.starry.mitsubachi.feature.map.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
private fun Content(
  viewModel: SearchMapScreenViewModel = hiltViewModel(),
) {
  val permissionState = rememberPermissionState(AndroidPermission.Location)
  LaunchedEffect(Unit) {
    if (permissionState.status != PermissionStatus.Granted) {
      permissionState.launchPermissionRequester()
    }
  }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM_LEVEL)
  }

  val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()

  // ユーザーの現在地を取得して、カメラを移動
  LaunchedEffect(permissionState.status) {
    if (permissionState.status == PermissionStatus.Granted) {
      viewModel.loadCurrentLocation()
    }
  }

  // 現在地が取得できたらカメラを移動
  LaunchedEffect(currentLocation) {
    currentLocation?.let { location ->
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
      myLocationButtonEnabled = true,
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
@Suppress("LongMethod") // TODO: リファクタリング
@OptIn(ExperimentalLayoutApi::class)
private fun BottomSheetContent(
  modifier: Modifier = Modifier,
  viewModel: SearchMapScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val selectedVenue by viewModel.selectedVenue.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()

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
          .padding(bottom = 8.dp)
          .clickable(role = Role.Button) { viewModel.selectVenue(null) },
      )

      // 選択されたベニューの詳細を表示
      VenueRecommendationCard(
        recommendation = selectedVenue,
        modifier = Modifier.padding(vertical = 8.dp),
      )
    } else {
      // 検索入力フィールド
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.updateSearchQuery(it) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        placeholder = { Text(stringResource(R.string.search_map_search_hint)) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = null)
            }
          }
        },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
      )

      // カテゴリーフィルターボタン
      FlowRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        CategoryFilterChip(
          label = stringResource(R.string.search_map_category_food),
          section = "food",
          selected = selectedSection == "food",
          onClick = { viewModel.selectSection(if (selectedSection == "food") null else "food") }
        )
        CategoryFilterChip(
          label = stringResource(R.string.search_map_category_coffee),
          section = "coffee",
          selected = selectedSection == "coffee",
          onClick = { viewModel.selectSection(if (selectedSection == "coffee") null else "coffee") }
        )
        CategoryFilterChip(
          label = stringResource(R.string.search_map_category_sights),
          section = "sights",
          selected = selectedSection == "sights",
          onClick = { viewModel.selectSection(if (selectedSection == "sights") null else "sights") }
        )
        CategoryFilterChip(
          label = stringResource(R.string.search_map_category_shops),
          section = "shops",
          selected = selectedSection == "shops",
          onClick = { viewModel.selectSection(if (selectedSection == "shops") null else "shops") }
        )
        CategoryFilterChip(
          label = stringResource(R.string.search_map_category_arts),
          section = "arts",
          selected = selectedSection == "arts",
          onClick = { viewModel.selectSection(if (selectedSection == "arts") null else "arts") }
        )
      }

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
          ErrorScreen(state.exception, {
            viewModel.updateCurrentLocation(state.lastLocation)
          })
        }
      }
    }
  }
}

@Composable
private fun CategoryFilterChip(
  label: String,
  section: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text(label) },
  )
}
