package blue.starry.mitsubachi.feature.map.ui.histories

import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class VenueHistoryClusterItem(private val history: VenueHistory) : ClusterItem {
  override val position: LatLng
    get() = LatLng(history.venue.location.latitude, history.venue.location.longitude)

  override val title: String
    get() = history.venue.name

  override val snippet: String
    get() = ""

  override val zIndex: Float
    get() = history.count.toFloat()
}
