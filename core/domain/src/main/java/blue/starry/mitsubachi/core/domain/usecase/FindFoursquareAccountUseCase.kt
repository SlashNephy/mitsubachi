package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindFoursquareAccountUseCase @Inject constructor(
  private val foursquareAccountRepository: FoursquareAccountRepository,
) {
  suspend operator fun invoke(): FoursquareAccount? {
    return foursquareAccountRepository.primary.first()
  }
}
