package blue.starry.mitsubachi.feature.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun MapScreen(latitude: Double, longitude: Double, title: String?) { // TODO: Coordinates で表現する
  val location = LatLng(latitude, longitude)
  val markerState = rememberUpdatedMarkerState(position = location)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(location, 17f)
  }

  GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
  ) {
    if (title != null) {
      Marker(
        state = markerState,
        title = title,
        // snippet = "Marker in $title"
      )
    }
  }
}
