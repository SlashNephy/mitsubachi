package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.foursquare.VenueHistory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchUserVenueHistoriesUseCase @Inject constructor(private val client: FoursquareApiClient) {
  suspend operator fun invoke(): List<VenueHistory> {
    return client.getUserVenueHistories()
  }
}
