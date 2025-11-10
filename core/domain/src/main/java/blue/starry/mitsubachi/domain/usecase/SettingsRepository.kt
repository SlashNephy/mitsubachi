package blue.starry.mitsubachi.domain.usecase

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
  val isFirebaseCrashlyticsEnabled: Flow<Boolean>

  suspend fun setFirebaseCrashlyticsEnabled(isEnabled: Boolean)
}
