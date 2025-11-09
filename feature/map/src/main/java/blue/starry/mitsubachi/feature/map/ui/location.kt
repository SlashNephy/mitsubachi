package blue.starry.mitsubachi.feature.map.ui

import blue.starry.mitsubachi.domain.model.DeviceLocation
import com.google.android.gms.maps.model.LatLng

fun DeviceLocation.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
}
