package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.network.GoogleWebFontClientImpl
import blue.starry.mitsubachi.core.domain.usecase.GoogleWebFontClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class GoogleWebFontClientModule {
  @Binds
  @Singleton
  abstract fun bind(impl: GoogleWebFontClientImpl): GoogleWebFontClient
}
