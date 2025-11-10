package blue.starry.mitsubachi.data.repository

import androidx.datastore.core.DataStore
import blue.starry.mitsubachi.data.datastore.AppSettings
import blue.starry.mitsubachi.domain.usecase.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsRepositoryImpl @Inject constructor(
  private val dataStore: DataStore<AppSettings>,
) : SettingsRepository {
  override val isFirebaseCrashlyticsEnabled: Flow<Boolean> = dataStore.data.map { settings ->
    if (settings.hasIsFirebaseCrashlyticsEnabled()) {
      settings.isFirebaseCrashlyticsEnabled
    } else {
      true
    }
  }

  override suspend fun setFirebaseCrashlyticsEnabled(isEnabled: Boolean) {
    dataStore.updateData { settings ->
      settings.toBuilder()
        .setIsFirebaseCrashlyticsEnabled(isEnabled)
        .build()
    }
  }
}
