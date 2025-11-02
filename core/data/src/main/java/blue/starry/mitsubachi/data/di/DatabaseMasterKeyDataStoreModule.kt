package blue.starry.mitsubachi.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
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
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): DataStore<DatabaseMasterKey> {
    return MultiProcessDataStoreFactory.create(
      serializer = DatabaseMasterKeySerializer,
      corruptionHandler = ReplaceFileCorruptionHandler {
        DatabaseMasterKey.getDefaultInstance()
      },
      produceFile = {
        context.dataStoreFile("database_master_key.pb")
      },
    )
  }
}
