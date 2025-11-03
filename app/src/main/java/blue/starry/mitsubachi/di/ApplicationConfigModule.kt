package blue.starry.mitsubachi.di

import blue.starry.mitsubachi.BuildConfig
import blue.starry.mitsubachi.domain.model.ApplicationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApplicationConfigModule {
  @Provides
  @Singleton
  fun provide(): ApplicationConfig {
    return ApplicationConfig(
      versionName = BuildConfig.VERSION_NAME,
      versionCode = BuildConfig.VERSION_CODE,
      buildType = BuildConfig.BUILD_TYPE,
      flavor = BuildConfig.FLAVOR,
      isDebugBuild = BuildConfig.DEBUG,
      foursquareClientId = BuildConfig.foursquare_client_id,
      foursquareClientSecret = BuildConfig.foursquare_client_secret,
      foursquareRedirectUri = BuildConfig.foursquare_redirect_uri,
    )
  }
}
