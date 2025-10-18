package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.FoursquareAccount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindFoursquareAccountUseCase @Inject constructor(
  private val foursquareAccountRepository: FoursquareAccountRepository,
) {
  suspend operator fun invoke(): FoursquareAccount? {
    return foursquareAccountRepository.list().firstOrNull()
  }
}
