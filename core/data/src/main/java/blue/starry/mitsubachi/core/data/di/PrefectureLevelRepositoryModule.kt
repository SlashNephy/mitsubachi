package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.repository.PrefectureLevelRepositoryImpl
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLevelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PrefectureLevelRepositoryModule {
  @Binds
  @Singleton
  internal abstract fun bind(impl: PrefectureLevelRepositoryImpl): PrefectureLevelRepository
}
