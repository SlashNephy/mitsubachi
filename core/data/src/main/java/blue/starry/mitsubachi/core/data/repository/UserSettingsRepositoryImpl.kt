package blue.starry.mitsubachi.core.data.repository

import androidx.room.withTransaction
import blue.starry.mitsubachi.core.data.database.MitsubachiDatabase
import blue.starry.mitsubachi.core.data.database.dao.UserSettingsDao
import blue.starry.mitsubachi.core.data.database.entity.UserSettings
import blue.starry.mitsubachi.core.data.repository.model.toDomain
import blue.starry.mitsubachi.core.data.repository.model.toEntity
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.usecase.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.core.domain.model.UserSettings as DomainUserSettings

@Singleton
internal class UserSettingsRepositoryImpl @Inject constructor(
  private val database: MitsubachiDatabase,
  private val dao: UserSettingsDao,
) : UserSettingsRepository {
  override fun flow(account: FoursquareAccount): Flow<DomainUserSettings> {
    return dao.findByFoursquareAccountId(account.id)
      .map { it?.payload?.toDomain() ?: DomainUserSettings.Default }
  }

  override suspend fun <R> select(
    account: FoursquareAccount,
    block: (DomainUserSettings) -> R,
  ): R {
    return flow(account).map(block).first()
  }

  override suspend fun update(
    account: FoursquareAccount,
    block: (DomainUserSettings) -> DomainUserSettings,
  ) {
    database.withTransaction {
      val oldEntity = dao.findByFoursquareAccountId(account.id).first()?.payload
      val newEntity = (oldEntity?.toDomain() ?: DomainUserSettings.Default)
        .let(block)
        .toEntity()

      dao.insertOrUpdate(
        UserSettings(
          foursquareAccountId = account.id,
          payload = newEntity,
        ),
      )
    }
  }
}
