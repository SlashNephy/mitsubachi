package blue.starry.mitsubachi.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(
  private val foursquareAccountRepository: FoursquareAccountRepository,
) {
  suspend operator fun invoke() {
    // TODO: 複数アカウント対応
    val account = foursquareAccountRepository.list().firstOrNull() ?: return

    foursquareAccountRepository.delete(account)
  }
}
