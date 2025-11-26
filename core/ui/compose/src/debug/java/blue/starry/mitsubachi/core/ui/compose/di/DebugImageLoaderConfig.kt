package blue.starry.mitsubachi.core.ui.compose.di

import coil3.ImageLoader
import coil3.util.DebugLogger

internal object DebugImageLoaderConfig : ImageLoaderConfig {
  override fun ImageLoader.Builder.configure() {
    logger(DebugLogger())
  }
}
