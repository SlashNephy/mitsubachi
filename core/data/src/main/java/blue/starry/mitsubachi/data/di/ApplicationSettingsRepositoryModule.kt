package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.ApplicationSettingsRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.ApplicationSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ApplicationSettingsRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: ApplicationSettingsRepositoryImpl): ApplicationSettingsRepository
}
