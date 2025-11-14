package blue.starry.mitsubachi.core.ui.compose.di

import blue.starry.mitsubachi.core.ui.compose.error.ErrorFormatter
import blue.starry.mitsubachi.core.ui.compose.error.ErrorFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ErrorFormatterModule {
  @Binds
  @Singleton
  abstract fun bind(impl: ErrorFormatterImpl): ErrorFormatter
}
