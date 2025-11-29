package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import kotlinx.coroutines.flow.Flow

interface FoursquareAccountRepository {
  val primary: Flow<FoursquareAccount?>
  suspend fun list(): List<FoursquareAccount>
  suspend fun update(account: FoursquareAccount)
  suspend fun delete(account: FoursquareAccount)
}
