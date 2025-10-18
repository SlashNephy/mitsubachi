package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.repository.FoursquareAccountRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoursquareAccountRepositoryModule {
  @Binds
  @Singleton
  abstract fun bind(impl: FoursquareAccountRepositoryImpl): FoursquareAccountRepository
}
