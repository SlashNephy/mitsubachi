package blue.starry.mitsubachi.feature.map.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.DefaultMapUiSettings
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

@Composable
internal fun Map(
  cameraPositionState: CameraPositionState,
  modifier: Modifier = Modifier,
  properties: MapProperties = DefaultMapProperties,
  uiSettings: MapUiSettings = DefaultMapUiSettings,
  content:
  @Composable @GoogleMapComposable () -> Unit,
) {
  GoogleMap(
    modifier = modifier,
    cameraPositionState = cameraPositionState,
    contentDescription = "マップ",
    properties = properties,
    uiSettings = uiSettings,
    mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
    content = content,
  )
}
