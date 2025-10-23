package blue.starry.mitsubachi.ui.feature.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(latitude: Double, longitude: Double) {
  val location = LatLng(latitude, longitude)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(location, 10f)
  }

  GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
  )
}
