package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.LocationTrackingRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.LocationTrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocationTrackingModule {
  @Binds
  @Singleton
  abstract fun bind(impl: LocationTrackingRepositoryImpl): LocationTrackingRepository
}
