package blue.starry.mitsubachi.core.domain.usecase

import java.nio.file.Path

interface ImageDownloader {
  suspend fun download(url: String): Path?
}
