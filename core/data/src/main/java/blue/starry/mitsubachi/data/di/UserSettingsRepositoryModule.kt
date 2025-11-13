package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.UserSettingsRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserSettingsRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: UserSettingsRepositoryImpl): UserSettingsRepository
}
