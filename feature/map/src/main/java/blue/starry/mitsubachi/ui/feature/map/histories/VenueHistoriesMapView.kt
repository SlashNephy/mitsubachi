package blue.starry.mitsubachi.ui.feature.map.histories

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.domain.model.foursquare.VenueHistory
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.clustering.rememberClusterRenderer

@Composable
@OptIn(MapsComposeExperimentalApi::class)
fun VenueHistoriesMapView(histories: List<VenueHistory>) {
  val clusterManager = rememberClusterManager<VenueHistoryClusterItem>() ?: return
  val renderer = rememberClusterRenderer(
    clusterManager = clusterManager,
    clusterContent = null, // TODO: クラスタリング時の見た目を実装
    // MarkerComposableのContentをそのまま実装する
    clusterItemContent = {
      MapPoint(modifier = Modifier.size(8.dp))
    },
  ) ?: return

  LaunchedEffect(clusterManager) {
    // TODO: クリック時の処理 (Venue の詳細画面へ)
    // clusterManager.setOnClusterItemClickListener {
    //   true
    // }

    clusterManager.setAnimation(false)
    clusterManager.algorithm = ZoomThresholdAlgorithm(
      baseAlgorithm = NonHierarchicalDistanceBasedAlgorithm(),
      zoomThreshold = DEFAULT_CLUSTERING_THRESHOLD_ZOOM_LEVEL,
    )
  }

  LaunchedEffect(clusterManager, renderer) {
    if (clusterManager.renderer != renderer) {
      clusterManager.renderer = renderer
    }
  }

  val items = remember { histories.map { VenueHistoryClusterItem(it) } }
  Clustering(
    items = items,
    clusterManager = clusterManager,
  )
}
