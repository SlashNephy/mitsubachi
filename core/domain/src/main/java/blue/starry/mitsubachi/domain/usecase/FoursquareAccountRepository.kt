package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.FoursquareAccount

interface FoursquareAccountRepository {
  suspend fun list(): List<FoursquareAccount>
  suspend fun update(account: FoursquareAccount)
  suspend fun delete(account: FoursquareAccount)
}
