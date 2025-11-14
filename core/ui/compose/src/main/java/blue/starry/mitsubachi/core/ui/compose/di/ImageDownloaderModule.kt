package blue.starry.mitsubachi.core.ui.compose.di

import blue.starry.mitsubachi.core.domain.usecase.ImageDownloader
import blue.starry.mitsubachi.core.ui.compose.image.ImageDownloaderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ImageDownloaderModule {
  @Binds
  @Singleton
  abstract fun bind(impl: ImageDownloaderImpl): ImageDownloader
}
