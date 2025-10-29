package blue.starry.mitsubachi.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToggleLikeCheckInUseCase @Inject constructor(
  private val likeCheckInUseCase: LikeCheckInUseCase,
) {
  suspend operator fun invoke(checkInId: String, isLiked: Boolean) {
    if (isLiked) {
      throw UnlikeNotImplementedException()
    } else {
      likeCheckInUseCase(checkInId)
    }
  }
}

class UnlikeNotImplementedException : Exception("Unlike feature is not yet implemented")
