package blue.starry.mitsubachi.feature.widget.photo.state

import android.net.Uri
import androidx.annotation.DrawableRes
import blue.starry.mitsubachi.core.common.UriSerializer
import blue.starry.mitsubachi.core.ui.glance.state.WidgetState
import kotlinx.serialization.Serializable

@Serializable
sealed interface PhotoWidgetState : WidgetState {
  @Serializable
  data object Loading : PhotoWidgetState

  @Serializable
  data class Photo(
    val id: String,
    val image: Image,
    @Serializable(with = UriSerializer::class) val checkInUri: Uri,
    val venueName: String,
    val venueAddress: String,
    val date: String,
  ) : PhotoWidgetState {
    @Serializable
    sealed interface Image {
      @Serializable
      data class Local(val path: String) : Image

      @Serializable
      data class Resource(@param:DrawableRes val id: Int) : Image
    }
  }

  @Serializable
  data object NoPhotos : PhotoWidgetState

  @Serializable
  data object LoginRequired : PhotoWidgetState
}
