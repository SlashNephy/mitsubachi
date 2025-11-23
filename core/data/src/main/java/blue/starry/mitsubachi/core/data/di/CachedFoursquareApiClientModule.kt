package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.network.CachedFoursquareApiClientImpl
import blue.starry.mitsubachi.core.domain.usecase.CachedFoursquareApiClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CachedFoursquareApiClientModule {
  @Binds
  @Singleton
  internal abstract fun bind(impl: CachedFoursquareApiClientImpl): CachedFoursquareApiClient
}
