package blue.starry.mitsubachi.ui.feature.checkin

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.FilePart
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.usecase.CreateCheckInUseCase
import blue.starry.mitsubachi.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SHOUT_MAX_LENGTH = 160

@HiltViewModel
class CreateCheckInScreenViewModel @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val createCheckInUseCase: CreateCheckInUseCase,
  private val uploadImageUseCase: UploadImageUseCase,
) : ViewModel() {
  data class ShoutState(
    val value: String,
    val remainingLength: Int,
    val hasError: Boolean,
  ) {
    val valueOrNull: String?
      get() = value.ifBlank { null }
  }

  private val _state = MutableStateFlow(
    ShoutState(value = "", remainingLength = SHOUT_MAX_LENGTH, hasError = false),
  )
  val state = _state.asStateFlow()

  fun createCheckIn(
    venue: Venue,
    shout: String? = null,
    isPublic: Boolean = true,
    imageUris: List<Uri> = emptyList(),
  ): Job {
    return viewModelScope.launch {
      val checkIn = createCheckInUseCase(venue, shout, isPublic)
      if (imageUris.isNotEmpty()) {
        val files = loadImages(imageUris)
        uploadImageUseCase(checkIn.id, files, isPublic)
      }
    }
  }

  private fun loadImage(uri: Uri): FilePart? {
    val stream = context.contentResolver.openInputStream(uri) ?: return null

    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    val cursor = context.contentResolver.query(uri, projection, null, null, null) ?: return null
    val filename = cursor.use { cursor ->
      if (cursor.moveToFirst()) {
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0) {
          cursor.getString(index)
        } else {
          null
        }
      } else {
        null
      }
    }

    return FilePart(
      data = stream.use { it.readBytes() },
      fileName = filename ?: "image.jpg",
      contentType = context.contentResolver.getType(uri),
    )
  }

  private suspend fun loadImages(uris: List<Uri>): List<FilePart> {
    return uris
      .map { uri ->
        viewModelScope.async(Dispatchers.IO) {
          loadImage(uri)
        }
      }
      .awaitAll()
      .filterNotNull()
  }

  fun onShoutUpdated(shout: String) {
    _state.value = ShoutState(
      value = shout,
      remainingLength = SHOUT_MAX_LENGTH - countShoutLength(shout),
      hasError = !validateShout(shout),
    )
  }

  private fun countShoutLength(shout: String): Int {
    return shout.codePointCount(0, shout.length)
  }

  private fun validateShout(shout: String): Boolean {
    return countShoutLength(shout) <= SHOUT_MAX_LENGTH
  }
}
