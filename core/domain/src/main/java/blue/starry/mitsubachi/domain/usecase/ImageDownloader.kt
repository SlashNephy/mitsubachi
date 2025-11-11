package blue.starry.mitsubachi.domain.usecase

import java.nio.file.Path

interface ImageDownloader {
  suspend fun download(url: String): Path?
}
