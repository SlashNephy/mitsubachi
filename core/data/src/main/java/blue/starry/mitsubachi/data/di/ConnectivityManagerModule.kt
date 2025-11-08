package blue.starry.mitsubachi.data.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ConnectivityManagerModule {
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): ConnectivityManager {
    return context.getSystemService<ConnectivityManager>()
      ?: error("ConnectivityManager unavailable")
  }
}
