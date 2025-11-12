package blue.starry.mitsubachi.data.repository

import androidx.datastore.core.DataStore
import blue.starry.mitsubachi.data.datastore.ApplicationSettings
import blue.starry.mitsubachi.data.repository.model.toDomain
import blue.starry.mitsubachi.data.repository.model.toEntity
import blue.starry.mitsubachi.domain.usecase.ApplicationSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.domain.model.ApplicationSettings as DomainApplicationSettings

@Singleton
internal class ApplicationSettingsRepositoryImpl @Inject constructor(
  private val dataStore: DataStore<ApplicationSettings>,
) : ApplicationSettingsRepository {
  override val flow: Flow<DomainApplicationSettings> =
    dataStore.data.map(ApplicationSettings::toDomain)

  override suspend fun update(block: (DomainApplicationSettings) -> DomainApplicationSettings) {
    dataStore.updateData {
      it.toDomain().let(block).toEntity()
    }
  }
}
