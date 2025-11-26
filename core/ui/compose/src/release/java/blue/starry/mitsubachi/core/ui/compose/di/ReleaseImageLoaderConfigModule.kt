package blue.starry.mitsubachi.core.ui.compose.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ReleaseImageLoaderConfigModule {
    @Provides
    @Singleton
    fun provide(): ImageLoaderConfig {
        return ReleaseImageLoaderConfig
    }
}
