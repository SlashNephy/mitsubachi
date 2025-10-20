package blue.starry.mitsubachi.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import blue.starry.mitsubachi.data.datastore.DatabaseMasterKey
import blue.starry.mitsubachi.data.datastore.DatabaseMasterKeySerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseMasterKeyDataStoreModule {
  private val Context.dataStore: DataStore<DatabaseMasterKey> by dataStore(
    fileName = "database_master_key.pb",
    serializer = DatabaseMasterKeySerializer
  )

  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): DataStore<DatabaseMasterKey> {
    return context.dataStore
  }
}
