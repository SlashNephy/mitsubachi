package blue.starry.mitsubachi.data.repository

import androidx.datastore.core.DataStore
import blue.starry.mitsubachi.data.datastore.UserSettings
import blue.starry.mitsubachi.data.datastore.UserSettingsMap
import blue.starry.mitsubachi.data.datastore.copy
import blue.starry.mitsubachi.data.repository.model.toDomain
import blue.starry.mitsubachi.data.repository.model.toEntity
import blue.starry.mitsubachi.domain.usecase.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.domain.model.UserSettings as DomainUserSettings

@Singleton
internal class UserSettingsRepositoryImpl @Inject constructor(
  private val dataStore: DataStore<UserSettingsMap>,
) : UserSettingsRepository {
  private val flow =
    dataStore.data.map { it.usersMap.entries.associate { (key, value) -> key to value.toDomain() } }

  override fun flow(userId: String): Flow<DomainUserSettings> {
    return flow.map { it[userId] ?: UserSettings.getDefaultInstance().toDomain() }
  }

  override suspend fun update(userId: String, block: (DomainUserSettings) -> DomainUserSettings) {
    dataStore.updateData {
      it.copy {
        users[userId] = (users[userId] ?: UserSettings.getDefaultInstance())
          .toDomain()
          .let(block)
          .toEntity()
      }
    }
  }
}
