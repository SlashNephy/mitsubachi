package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.network.FoursquareApiClientFactoryImpl
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClientFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FoursquareApiClientFactoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: FoursquareApiClientFactoryImpl): FoursquareApiClientFactory
}
