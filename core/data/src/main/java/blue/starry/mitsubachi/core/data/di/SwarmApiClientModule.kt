package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.network.SwarmApiClientImpl
import blue.starry.mitsubachi.core.domain.usecase.SwarmApiClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SwarmApiClientModule {
  @Binds
  @Singleton
  abstract fun bind(impl: SwarmApiClientImpl): SwarmApiClient
}
