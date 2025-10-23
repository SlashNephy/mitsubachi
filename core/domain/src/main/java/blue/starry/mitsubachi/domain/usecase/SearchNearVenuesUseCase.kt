package blue.starry.mitsubachi.domain.usecase

import android.Manifest
import androidx.annotation.RequiresPermission
import blue.starry.mitsubachi.domain.model.Coordinates
import blue.starry.mitsubachi.domain.model.Venue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class SearchNearVenuesUseCase @Inject constructor(
  private val client: FoursquareApiClient,
  private val deviceLocationRepository: DeviceLocationRepository,
) {
  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
  suspend operator fun invoke(query: String? = null): List<Venue> {
    val location = deviceLocationRepository.findCurrentLocation(timeout = 10.seconds)
      ?: deviceLocationRepository.findLastLocation(timeout = 5.seconds)
    if (location == null) {
      return emptyList()
    }

    return invoke(
      coordinates = Coordinates(latitude = location.latitude, longitude = location.longitude),
      query = query,
    )
  }

  suspend operator fun invoke(
    coordinates: Coordinates,
    query: String? = null,
  ): List<Venue> {
    return client.searchNearVenues(coordinates, query)
  }
}
