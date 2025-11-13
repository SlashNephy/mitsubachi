package blue.starry.mitsubachi.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import blue.starry.mitsubachi.data.datastore.UserSettingsMap
import blue.starry.mitsubachi.data.datastore.UserSettingsMapSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object UserSettingsMapDataStoreModule {
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): DataStore<UserSettingsMap> {
    return MultiProcessDataStoreFactory.create(
      serializer = UserSettingsMapSerializer,
      corruptionHandler = ReplaceFileCorruptionHandler {
        UserSettingsMap.getDefaultInstance()
      },
      produceFile = {
        context.dataStoreFile("user_settings.pb")
      },
    )
  }
}
