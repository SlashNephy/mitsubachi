package blue.starry.mitsubachi.ui.di

import blue.starry.mitsubachi.ui.error.ErrorReporter
import blue.starry.mitsubachi.ui.error.ErrorReporterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ErrorReporterModule {
  @Binds
  @Singleton
  abstract fun bind(impl: ErrorReporterImpl): ErrorReporter
}
