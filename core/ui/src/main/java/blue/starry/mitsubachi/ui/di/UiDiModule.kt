package blue.starry.mitsubachi.ui.di

import android.icu.text.RelativeDateTimeFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiDiModule {
  @Provides
  @Singleton
  fun provideRelativeDateTimeFormatter(): RelativeDateTimeFormatter {
    return RelativeDateTimeFormatter.getInstance()
  }
}
