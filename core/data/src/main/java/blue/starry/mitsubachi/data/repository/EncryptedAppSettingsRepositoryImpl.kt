package blue.starry.mitsubachi.data.repository

import blue.starry.mitsubachi.data.database.EncryptedAppDatabase
import blue.starry.mitsubachi.domain.usecase.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.data.database.entity.AppSettings as EntityAppSettings

@Singleton
internal class EncryptedAppSettingsRepositoryImpl @Inject constructor(
  private val database: EncryptedAppDatabase,
) : AppSettingsRepository {
  override val isFirebaseCrashlyticsEnabled: Flow<Boolean> = database.appSettings()
    .getByUserId(DEFAULT_USER_ID)
    .map { settings ->
      settings?.isFirebaseCrashlyticsEnabled ?: true
    }

  override suspend fun setFirebaseCrashlyticsEnabled(isEnabled: Boolean) {
    val entity = EntityAppSettings(
      userId = DEFAULT_USER_ID,
      isFirebaseCrashlyticsEnabled = isEnabled,
    )
    database.appSettings().insertOrUpdate(entity)
  }

  companion object {
    private const val DEFAULT_USER_ID = "default"
  }
}
