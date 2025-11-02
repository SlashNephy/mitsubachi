package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.Coordinates
import blue.starry.mitsubachi.domain.model.VenueRecommendation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchVenueRecommendationsUseCase @Inject constructor(
  private val client: FoursquareApiClient,
) {
  suspend operator fun invoke(
    coordinates: Coordinates,
  ): List<VenueRecommendation> {
    return client.searchVenueRecommendations(coordinates)
  }
}
