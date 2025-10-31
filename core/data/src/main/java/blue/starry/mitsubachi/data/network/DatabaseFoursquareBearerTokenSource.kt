package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountRepository
import blue.starry.mitsubachi.domain.usecase.FoursquareBearerTokenSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DatabaseFoursquareBearerTokenSource @Inject constructor(
  private val repository: FoursquareAccountRepository,
) : FoursquareBearerTokenSource {
  override suspend fun load(): String {
    // TODO: 複数アカウント対応
    val account = repository.list().firstOrNull() ?: throw UnauthorizedError

    return account.accessToken
  }
}
