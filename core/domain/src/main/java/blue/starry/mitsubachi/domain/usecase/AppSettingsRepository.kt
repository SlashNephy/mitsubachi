package blue.starry.mitsubachi.domain.usecase

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
  val isFirebaseCrashlyticsEnabled: Flow<Boolean>

  suspend fun setFirebaseCrashlyticsEnabled(isEnabled: Boolean)
}
