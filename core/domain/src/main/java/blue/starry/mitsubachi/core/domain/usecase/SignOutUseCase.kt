package blue.starry.mitsubachi.core.domain.usecase

import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(
  private val foursquareAccountRepository: FoursquareAccountRepository,
) {
  suspend operator fun invoke() {
    val account = foursquareAccountRepository.primary.first() ?: return

    foursquareAccountRepository.delete(account)
  }
}
