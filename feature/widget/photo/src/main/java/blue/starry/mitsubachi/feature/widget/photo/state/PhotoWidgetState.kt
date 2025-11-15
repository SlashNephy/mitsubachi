package blue.starry.mitsubachi.feature.widget.photo.state

import android.net.Uri
import blue.starry.mitsubachi.core.common.UriSerializer
import blue.starry.mitsubachi.core.common.ZonedDateTimeSerializer
import blue.starry.mitsubachi.core.ui.glance.state.WidgetState
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
sealed interface PhotoWidgetState : WidgetState {
  @Serializable
  data object Loading : PhotoWidgetState

  @Serializable
  data class Photo(
    val id: String,
    val path: String,
    @Serializable(with = UriSerializer::class) val checkInUri: Uri,
    val venueName: String,
    val venueAddress: String,
    @Serializable(with = ZonedDateTimeSerializer::class) val checkInAt: ZonedDateTime,
  ) : PhotoWidgetState

  @Serializable
  data object NoPhotos : PhotoWidgetState

  @Serializable
  data object LoginRequired : PhotoWidgetState
}
