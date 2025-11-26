package blue.starry.mitsubachi.core.ui.compose.di

import coil3.ImageLoader

internal fun interface ImageLoaderConfig {
    fun ImageLoader.Builder.configure()
}
