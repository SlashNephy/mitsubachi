package blue.starry.mitsubachi.ui.image

import android.content.Context
import blue.starry.mitsubachi.domain.usecase.ImageDownloader
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDownloaderImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val imageLoader: ImageLoader,
) : ImageDownloader {
  override suspend fun download(url: String): Path? {
    val request = ImageRequest.Builder(context)
      // 必ずディスクキャッシュを使用させる
      .diskCachePolicy(CachePolicy.ENABLED)
      .memoryCachePolicy(CachePolicy.DISABLED)
      .data(url)
      .build()

    return when (val result = imageLoader.execute(request)) {
      is SuccessResult -> {
        // ディスクキャッシュにダウンロードされたファイルの実体を探して返す
        result.diskCacheKey?.let { cacheKey ->
          imageLoader.diskCache?.openSnapshot(cacheKey)?.use {
            it.data.toNioPath()
          }
        }
      }

      is ErrorResult -> {
        Timber.w(result.throwable, "failed to download image: $url")

        null
      }
    }
  }
}
