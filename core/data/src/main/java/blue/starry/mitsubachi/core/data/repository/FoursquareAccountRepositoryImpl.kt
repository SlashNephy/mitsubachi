package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.EncryptedAppDatabase
import blue.starry.mitsubachi.core.data.database.entity.fromDomain
import blue.starry.mitsubachi.core.data.database.entity.toDomain
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.usecase.FoursquareAccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount as EntityFoursquareAccount

@Singleton
class FoursquareAccountRepositoryImpl @Inject constructor(private val database: EncryptedAppDatabase) :
  FoursquareAccountRepository {
  override suspend fun list(): List<FoursquareAccount> {
    return database.foursquareAccount().list().map(EntityFoursquareAccount::toDomain)
  }

  override suspend fun update(account: FoursquareAccount) {
    database.foursquareAccount().insertOrUpdate(EntityFoursquareAccount.fromDomain(account))
  }

  override suspend fun delete(account: FoursquareAccount) {
    database.foursquareAccount().deleteById(account.id)
  }
}
