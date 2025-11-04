package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.AppSettingsRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.AppSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppSettingsRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: AppSettingsRepositoryImpl): AppSettingsRepository
}
