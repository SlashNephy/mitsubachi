package blue.starry.mitsubachi.feature.map.ui.histories

import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class VenueHistoryClusterItem(private val history: VenueHistory) : ClusterItem {
  override fun getPosition(): LatLng {
    return LatLng(history.venue.location.latitude, history.venue.location.longitude)
  }

  override fun getTitle(): String {
    return history.venue.name
  }

  override fun getSnippet(): String {
    return ""
  }

  override fun getZIndex(): Float {
    return history.count.toFloat()
  }
}
