package blue.starry.mitsubachi.ui.di

import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatter
import blue.starry.mitsubachi.ui.formatter.RelativeDateTimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.icu.text.RelativeDateTimeFormatter as AndroidRelativeDateTimeFormatter

@Module
@InstallIn(SingletonComponent::class)
abstract class RelativeDateTimeFormatterModule {
  companion object {
    @Provides
    @Singleton
    fun provideRelativeDateTimeFormatter(): AndroidRelativeDateTimeFormatter {
      return AndroidRelativeDateTimeFormatter.getInstance()
    }
  }

  @Binds
  @Singleton
  abstract fun bind(impl: RelativeDateTimeFormatterImpl): RelativeDateTimeFormatter
}
