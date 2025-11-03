package blue.starry.mitsubachi.domain.usecase

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
  val crashlyticsEnabled: Flow<Boolean>

  suspend fun setCrashlyticsEnabled(enabled: Boolean)
}
