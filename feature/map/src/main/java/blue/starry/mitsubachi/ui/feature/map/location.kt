package blue.starry.mitsubachi.ui.feature.map

import blue.starry.mitsubachi.domain.model.DeviceLocation
import com.google.android.gms.maps.model.LatLng

fun DeviceLocation.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
}
