package blue.starry.mitsubachi.data.repository

import blue.starry.mitsubachi.data.database.EncryptedAppDatabase
import blue.starry.mitsubachi.data.database.entity.fromDomain
import blue.starry.mitsubachi.data.database.entity.toDomain
import blue.starry.mitsubachi.domain.model.FoursquareAccount
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountEvent
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.data.database.entity.FoursquareAccount as EntityFoursquareAccount

@Singleton
class FoursquareAccountRepositoryImpl @Inject constructor(private val database: EncryptedAppDatabase) :
  FoursquareAccountRepository {
  private val _events = MutableSharedFlow<FoursquareAccountEvent>()
  override val events = _events.asSharedFlow()

  override suspend fun list(): List<FoursquareAccount> {
    return database.foursquareAccount().list().map(EntityFoursquareAccount::toDomain)
  }

  override suspend fun update(account: FoursquareAccount) {
    database.foursquareAccount().insertOrUpdate(EntityFoursquareAccount.fromDomain(account))
    _events.emit(FoursquareAccountEvent.Updated(account))
  }

  override suspend fun delete(account: FoursquareAccount) {
    database.foursquareAccount().deleteById(account.id)
    _events.emit(FoursquareAccountEvent.Deleted(account))
  }
}
