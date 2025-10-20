package blue.starry.mitsubachi.domain.usecase

import android.Manifest
import androidx.annotation.RequiresPermission
import blue.starry.mitsubachi.domain.model.Venue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchNearVenuesUseCase @Inject constructor(
  private val client: FoursquareApiClient,
  private val deviceLocationRepository: DeviceLocationRepository,
) {
  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
  suspend operator fun invoke(query: String? = null): List<Venue> {
    val location = deviceLocationRepository.findCurrentLocation()
    if (location == null) {
      return emptyList()
    }

    return invoke(location.latitude, location.longitude, query)
  }

  suspend operator fun invoke(
    latitude: Double,
    longitude: Double,
    query: String? = null,
  ): List<Venue> {
    return client.searchNearVenues(latitude, longitude, query)
  }
}
