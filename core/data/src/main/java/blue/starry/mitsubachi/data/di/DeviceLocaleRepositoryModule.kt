package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.DeviceLocaleRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.DeviceLocaleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DeviceLocaleRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: DeviceLocaleRepositoryImpl): DeviceLocaleRepository
}
