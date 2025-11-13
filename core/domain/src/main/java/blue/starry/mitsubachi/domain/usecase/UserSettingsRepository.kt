package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
  fun flow(userId: String): Flow<UserSettings>

  suspend fun update(userId: String, block: (UserSettings) -> UserSettings)
}
