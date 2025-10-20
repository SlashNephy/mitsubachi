package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.network.FoursquareApiClientImpl
import blue.starry.mitsubachi.domain.usecase.FoursquareApiClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoursquareApiClientModule {
  @Binds
  @Singleton
  abstract fun bind(impl: FoursquareApiClientImpl): FoursquareApiClient
}
