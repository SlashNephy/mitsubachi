package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount
import blue.starry.mitsubachi.core.data.repository.model.toDomain
import blue.starry.mitsubachi.core.data.repository.model.toEntity
import blue.starry.mitsubachi.core.domain.usecase.FoursquareAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount as DomainFoursquareAccount

@Singleton
class FoursquareAccountRepositoryImpl @Inject constructor(
  private val dao: FoursquareAccountDao,
) : FoursquareAccountRepository {
  override val primary: Flow<DomainFoursquareAccount?> = dao.primary.map { it?.toDomain() }

  override suspend fun list(): List<DomainFoursquareAccount> {
    return dao.list().map(FoursquareAccount::toDomain)
  }

  override suspend fun update(account: DomainFoursquareAccount) {
    dao.insertOrUpdate(account.toEntity())
  }

  override suspend fun delete(account: DomainFoursquareAccount) {
    dao.deleteById(account.id)
  }
}
