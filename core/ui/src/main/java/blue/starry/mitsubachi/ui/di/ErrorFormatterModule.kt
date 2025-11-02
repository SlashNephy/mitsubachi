package blue.starry.mitsubachi.ui.di

import blue.starry.mitsubachi.ui.error.ErrorFormatter
import blue.starry.mitsubachi.ui.error.ErrorFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorFormatterModule {
  @Binds
  @Singleton
  abstract fun bind(impl: ErrorFormatterImpl): ErrorFormatter
}
