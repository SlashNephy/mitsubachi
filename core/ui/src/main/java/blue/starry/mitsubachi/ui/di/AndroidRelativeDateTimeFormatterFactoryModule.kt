package blue.starry.mitsubachi.ui.di

import blue.starry.mitsubachi.ui.formatter.AndroidRelativeDateTimeFormatterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.icu.text.RelativeDateTimeFormatter as AndroidRelativeDateTimeFormatter

@Module
@InstallIn(SingletonComponent::class)
internal object AndroidRelativeDateTimeFormatterFactoryModule {
  @Provides
  @Singleton
  fun provide(): AndroidRelativeDateTimeFormatterFactory {
    return AndroidRelativeDateTimeFormatterFactory {
      AndroidRelativeDateTimeFormatter.getInstance()
    }
  }
}
