package blue.starry.mitsubachi.core.ui.compose.di

import coil3.ImageLoader

fun interface ImageLoaderConfig {
  fun ImageLoader.Builder.apply()
}
