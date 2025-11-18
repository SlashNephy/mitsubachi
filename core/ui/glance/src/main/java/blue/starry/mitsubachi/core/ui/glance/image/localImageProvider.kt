package blue.starry.mitsubachi.core.ui.glance.image

import android.graphics.BitmapFactory
import androidx.glance.ImageProvider
import java.io.File

fun localImageProvider(path: String): ImageProvider? {
  val file = File(path)
  if (!file.exists()) {
    return null
  }

  return BitmapFactory.decodeFile(file.absolutePath)?.let {
    ImageProvider(it)
  }
}
