package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.repository.SecureUserSettingsRepositoryImpl
import blue.starry.mitsubachi.core.domain.usecase.SecureUserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecureSettingsRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: SecureUserSettingsRepositoryImpl): SecureUserSettingsRepository
}
