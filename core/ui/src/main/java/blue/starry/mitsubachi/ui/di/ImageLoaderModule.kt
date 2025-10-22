package blue.starry.mitsubachi.ui.di

import android.content.Context
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.memory.MemoryCache
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {
  @Provides
  @Singleton
  @OptIn(ExperimentalTime::class, ExperimentalCoilApi::class)
  fun provide(
    @ApplicationContext context: Context,
    httpClient: HttpClient,
  ): ImageLoader {
    return ImageLoader
      .Builder(context)
      .components {
        add(
          KtorNetworkFetcherFactory(
            httpClient = { httpClient },
            cacheStrategy = {
              CacheControlCacheStrategy()
            }
          ),
        )
      }
      .diskCachePolicy(CachePolicy.ENABLED)
      .memoryCache(
        MemoryCache
          .Builder()
          .maxSizePercent(context, 0.10)
          .build(),
      )
      .crossfade(true)
      .build()
  }
}
