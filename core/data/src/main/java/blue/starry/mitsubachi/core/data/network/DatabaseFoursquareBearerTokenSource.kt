package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.domain.error.UnauthorizedError
import blue.starry.mitsubachi.core.domain.usecase.FoursquareAccountRepository
import blue.starry.mitsubachi.core.domain.usecase.FoursquareBearerTokenSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DatabaseFoursquareBearerTokenSource @Inject constructor(
  private val repository: FoursquareAccountRepository,
) : FoursquareBearerTokenSource {
  override suspend fun load(): String {
    val account = repository.primary.first() ?: throw UnauthorizedError()

    return account.accessToken
  }
}
