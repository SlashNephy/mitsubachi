package blue.starry.mitsubachi.domain.usecase

import blue.starry.mitsubachi.domain.model.ApplicationSettings
import kotlinx.coroutines.flow.Flow

interface ApplicationSettingsRepository {
  val flow: Flow<ApplicationSettings>

  suspend fun update(block: (ApplicationSettings) -> ApplicationSettings)
}
