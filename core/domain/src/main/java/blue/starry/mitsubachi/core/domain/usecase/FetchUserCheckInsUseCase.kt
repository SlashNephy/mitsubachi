package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.CheckIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchUserCheckInsUseCase @Inject constructor(val client: FoursquareApiClient) {
  suspend operator fun invoke(
    userId: String? = null,
    limit: Int? = null,
    offset: Int? = null,
  ): List<CheckIn> {
    return client.getUserCheckIns(userId, limit, offset)
  }
}
