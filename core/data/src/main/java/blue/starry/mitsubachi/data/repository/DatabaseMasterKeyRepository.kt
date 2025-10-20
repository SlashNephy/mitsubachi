package blue.starry.mitsubachi.data.repository

import androidx.datastore.core.DataStore
import blue.starry.mitsubachi.data.datastore.DatabaseMasterKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DatabaseMasterKeyRepository @Inject constructor(
  private val dataStore: DataStore<DatabaseMasterKey>,
) {
  suspend fun get(): DatabaseMasterKey? {
    return dataStore.data.firstOrNull()
  }

  suspend fun set(value: DatabaseMasterKey) {
    dataStore.updateData { value }
  }
}
