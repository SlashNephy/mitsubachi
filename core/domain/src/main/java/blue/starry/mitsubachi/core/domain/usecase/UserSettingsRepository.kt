package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
  fun flow(userId: String): Flow<UserSettings>

  suspend fun update(userId: String, block: (UserSettings) -> UserSettings)
}
