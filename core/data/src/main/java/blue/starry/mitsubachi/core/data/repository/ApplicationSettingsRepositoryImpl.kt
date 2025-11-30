package blue.starry.mitsubachi.core.data.repository

import androidx.datastore.core.DataStore
import blue.starry.mitsubachi.core.data.datastore.ApplicationSettings
import blue.starry.mitsubachi.core.data.repository.model.toDomain
import blue.starry.mitsubachi.core.data.repository.model.toEntity
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings as DomainApplicationSettings

@Singleton
internal class ApplicationSettingsRepositoryImpl @Inject constructor(
  private val dataStore: DataStore<ApplicationSettings>,
) : ApplicationSettingsRepository {
  override val flow: Flow<DomainApplicationSettings> =
    dataStore.data.map(ApplicationSettings::toDomain)

  override suspend fun <T> select(block: (DomainApplicationSettings) -> T): T {
    return flow.map(block).first()
  }

  override suspend fun update(block: (DomainApplicationSettings) -> DomainApplicationSettings) {
    dataStore.updateData {
      it.toDomain().let(block).toEntity()
    }
  }
}
