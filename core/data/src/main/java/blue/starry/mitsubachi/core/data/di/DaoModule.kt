package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.database.MitsubachiDatabase
import blue.starry.mitsubachi.core.data.database.dao.CacheDao
import blue.starry.mitsubachi.core.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.core.data.database.dao.UserSettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
  @Provides
  @Singleton
  internal fun provideFoursquareAccountDao(database: MitsubachiDatabase): FoursquareAccountDao {
    return database.foursquareAccount()
  }

  @Provides
  @Singleton
  internal fun provideCacheDao(database: MitsubachiDatabase): CacheDao {
    return database.cache()
  }

  @Provides
  @Singleton
  internal fun provideUserSettingsDao(database: MitsubachiDatabase): UserSettingsDao {
    return database.userSettings()
  }
}
