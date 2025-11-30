package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import kotlinx.coroutines.flow.Flow

interface ApplicationSettingsRepository {
  val flow: Flow<ApplicationSettings>

  suspend fun <T> select(block: (ApplicationSettings) -> T): T

  suspend fun update(block: (ApplicationSettings) -> ApplicationSettings)
}
