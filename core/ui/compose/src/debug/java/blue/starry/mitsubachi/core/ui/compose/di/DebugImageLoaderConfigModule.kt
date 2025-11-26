package blue.starry.mitsubachi.core.ui.compose.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DebugImageLoaderConfigModule {
  @Provides
  @Singleton
  fun provide(): ImageLoaderConfig {
    return DebugImageLoaderConfig
  }
}
