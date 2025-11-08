package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.Photo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UseCase to fetch photos from the user's check-ins
 */
@Singleton
class FetchUserCheckInPhotosUseCase @Inject constructor(val client: FoursquareApiClient) {
  /**
   * Fetches photos from the user's check-ins
   * @param limit Maximum number of check-ins to fetch
   * @param offset Offset for pagination
   * @return List of photos from the user's check-ins
   */
  suspend operator fun invoke(limit: Int = 100, offset: Int = 0): Result<List<Photo>> {
    return runCatching {
      val checkIns = client.getUserCheckIns(userId = null, limit = limit, offset = offset)
      checkIns.flatMap { it.photos }.filterNot { it.url.isEmpty() }
    }
  }
}
