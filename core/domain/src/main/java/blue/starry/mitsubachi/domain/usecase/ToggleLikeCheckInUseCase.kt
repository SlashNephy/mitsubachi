package blue.starry.mitsubachi.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToggleLikeCheckInUseCase @Inject constructor(
  private val likeCheckInUseCase: LikeCheckInUseCase,
  private val unlikeCheckInUseCase: UnlikeCheckInUseCase,
) {
  suspend operator fun invoke(checkInId: String, isLiked: Boolean) {
    if (isLiked) {
      unlikeCheckInUseCase(checkInId)
    } else {
      likeCheckInUseCase(checkInId)
    }
  }
}
