package blue.starry.mitsubachi.ui.feature.map.histories

import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.algo.Algorithm
import com.google.maps.android.clustering.algo.StaticCluster
import timber.log.Timber

class ZoomThresholdAlgorithm<T : ClusterItem>(
  private val baseAlgorithm: Algorithm<T>,
  private val zoomThreshold: Float,
) : Algorithm<T> by baseAlgorithm {
  override fun getClusters(zoom: Float): Set<Cluster<T>> {
    Timber.Forest.d("getClusters: zoom=$zoom, zoomThreshold=$zoomThreshold")

    return if (zoom < zoomThreshold) {
      baseAlgorithm.getClusters(zoom)
    } else {
      getSparseClusters()
    }
  }

  private fun getSparseClusters(): Set<Cluster<T>> {
    return items.map { item ->
      StaticCluster<T>(item.position).apply { add(item) }
    }.toSet()
  }
}
