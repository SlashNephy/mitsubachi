package blue.starry.mitsubachi.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DebugKtorConfigModule {
  @Provides
  @Singleton
  fun provide(): KtorConfig {
    return DebugKtorConfig
  }
}
