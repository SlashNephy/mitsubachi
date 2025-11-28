package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
  fun flow(account: FoursquareAccount): Flow<UserSettings>

  suspend fun <R> select(account: FoursquareAccount, block: (UserSettings) -> R): R

  suspend fun update(
    account: FoursquareAccount,
    block: (UserSettings) -> UserSettings,
  )
}
