package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.core.data.database.entity.fromDomain
import blue.starry.mitsubachi.core.data.database.entity.toDomain
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.usecase.FoursquareAccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount as EntityFoursquareAccount

@Singleton
class FoursquareAccountRepositoryImpl @Inject constructor(private val dao: FoursquareAccountDao) :
  FoursquareAccountRepository {
  override suspend fun list(): List<FoursquareAccount> {
    return dao.list().map(EntityFoursquareAccount::toDomain)
  }

  override suspend fun update(account: FoursquareAccount) {
    dao.insertOrUpdate(EntityFoursquareAccount.fromDomain(account))
  }

  override suspend fun delete(account: FoursquareAccount) {
    dao.deleteById(account.id)
  }
}
