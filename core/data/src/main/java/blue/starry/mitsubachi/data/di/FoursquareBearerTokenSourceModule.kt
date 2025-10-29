package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.network.DatabaseFoursquareBearerTokenSource
import blue.starry.mitsubachi.data.network.FoursquareBearerTokenSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoursquareBearerTokenSourceModule {
  @Binds
  @Singleton
  abstract fun bind(impl: DatabaseFoursquareBearerTokenSource): FoursquareBearerTokenSource
}
