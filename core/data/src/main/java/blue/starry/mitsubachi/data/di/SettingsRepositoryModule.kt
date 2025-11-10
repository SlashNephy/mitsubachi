package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.SettingsRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: SettingsRepositoryImpl): SettingsRepository
}
