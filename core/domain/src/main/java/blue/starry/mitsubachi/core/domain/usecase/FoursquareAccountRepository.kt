package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FoursquareAccount

interface FoursquareAccountRepository {
  suspend fun list(): List<FoursquareAccount>
  suspend fun update(account: FoursquareAccount)
  suspend fun delete(account: FoursquareAccount)
}
