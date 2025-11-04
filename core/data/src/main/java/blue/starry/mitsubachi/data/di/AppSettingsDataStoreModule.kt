package blue.starry.mitsubachi.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import blue.starry.mitsubachi.data.datastore.AppSettings
import blue.starry.mitsubachi.data.datastore.AppSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppSettingsDataStoreModule {
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): DataStore<AppSettings> {
    return MultiProcessDataStoreFactory.create(
      serializer = AppSettingsSerializer,
      corruptionHandler = ReplaceFileCorruptionHandler {
        AppSettings.getDefaultInstance()
      },
      produceFile = {
        context.dataStoreFile("app_settings.pb")
      },
    )
  }
}
