package blue.starry.mitsubachi.core.ui.compose.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler

// https://coil-kt.github.io/coil/compose/#previews
@OptIn(ExperimentalCoilApi::class)
private val previewHandler = AsyncImagePreviewHandler {
  ColorImage(Color.Cyan.toArgb())
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewImageProvider(content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
    content()
  }
}
