package blue.starry.mitsubachi.core.data.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NotificationManagerModule {
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): NotificationManager {
    return context.getSystemService<NotificationManager>()
      ?: error("NotificationManager unavailable")
  }
}
