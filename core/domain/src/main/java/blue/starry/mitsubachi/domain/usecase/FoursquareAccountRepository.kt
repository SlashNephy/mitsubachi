package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.FoursquareAccount
import kotlinx.coroutines.flow.SharedFlow

interface FoursquareAccountRepository {
  val events: SharedFlow<FoursquareAccountEvent>

  suspend fun list(): List<FoursquareAccount>
  suspend fun update(account: FoursquareAccount)
  suspend fun delete(account: FoursquareAccount)
}

sealed interface FoursquareAccountEvent {
  data class Updated(val account: FoursquareAccount) : FoursquareAccountEvent
  data class Deleted(val account: FoursquareAccount) : FoursquareAccountEvent
}
