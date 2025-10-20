package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.CheckIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchFeedUseCase @Inject constructor(val client: FoursquareApiClient) {
  suspend operator fun invoke(): List<CheckIn> {
    return client.getRecentCheckIns()
  }
}
