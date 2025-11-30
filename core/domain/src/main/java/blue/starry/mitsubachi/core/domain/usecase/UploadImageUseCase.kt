package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FilePart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadImageUseCase @Inject constructor(
  private val client: FoursquareApiClient,
) {
  suspend operator fun invoke(checkInId: String, files: List<FilePart>, isPublic: Boolean) {
    if (files.isEmpty()) {
      error("files is empty")
    }

    // TODO: 順序？
    coroutineScope {
      files.map { file ->
        launch {
          client.addPhotoToCheckIn(
            checkInId = checkInId,
            image = file,
            isPublic = isPublic,
          )
        }
      }.joinAll()
    }
  }
}
