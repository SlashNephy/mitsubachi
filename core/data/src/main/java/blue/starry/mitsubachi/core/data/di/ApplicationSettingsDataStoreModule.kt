package blue.starry.mitsubachi.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import blue.starry.mitsubachi.core.data.datastore.ApplicationSettings
import blue.starry.mitsubachi.core.data.datastore.ApplicationSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApplicationSettingsDataStoreModule {
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): DataStore<ApplicationSettings> {
    return MultiProcessDataStoreFactory.create(
      serializer = ApplicationSettingsSerializer,
      corruptionHandler = ReplaceFileCorruptionHandler {
        ApplicationSettings.getDefaultInstance()
      },
      produceFile = {
        context.dataStoreFile("application_settings.pb")
      },
    )
  }
}
