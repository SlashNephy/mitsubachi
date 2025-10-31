package blue.starry.mitsubachi

import blue.starry.mitsubachi.domain.model.ApplicationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDiModule {
  @Provides
  @Singleton
  fun provideApplicationConfig(): ApplicationConfig {
    return ApplicationConfig(
      versionName = BuildConfig.VERSION_NAME,
      isDebugBuild = BuildConfig.DEBUG,
      foursquareClientId = BuildConfig.FOURSQUARE_CLIENT_ID,
      foursquareClientSecret = BuildConfig.FOURSQUARE_CLIENT_SECRET,
      foursquareRedirectUri = BuildConfig.FOURSQUARE_REDIRECT_URI,
    )
  }
}
