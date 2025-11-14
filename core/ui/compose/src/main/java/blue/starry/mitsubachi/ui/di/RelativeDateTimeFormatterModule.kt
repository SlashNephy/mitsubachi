package blue.starry.mitsubachi.ui.di

import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RelativeDateTimeFormatterModule {
  @Binds
  @Singleton
  abstract fun bind(impl: RelativeDateTimeFormatterImpl): RelativeDateTimeFormatter
}
