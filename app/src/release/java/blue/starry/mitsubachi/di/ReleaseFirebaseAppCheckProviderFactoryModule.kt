package blue.starry.mitsubachi.di

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ReleaseFirebaseAppCheckProviderFactoryModule {
  @Provides
  @Singleton
  fun provide(): AppCheckProviderFactory {
    return PlayIntegrityAppCheckProviderFactory.getInstance()
  }
}
