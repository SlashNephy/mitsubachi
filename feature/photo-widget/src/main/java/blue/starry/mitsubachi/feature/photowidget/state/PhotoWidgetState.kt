package blue.starry.mitsubachi.feature.photowidget.state

import blue.starry.mitsubachi.common.ZonedDateTimeSerializer
import blue.starry.mitsubachi.core.ui.widget.state.WidgetState
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
sealed interface PhotoWidgetState : WidgetState {
  @Serializable
  data object Loading : PhotoWidgetState

  @Serializable
  data class Photo(
    val id: String,
    val checkInId: String,
    val venueName: String,
    @Serializable(with = ZonedDateTimeSerializer::class) val checkInAt: ZonedDateTime,
  ) : PhotoWidgetState

  @Serializable
  data object NoPhotos : PhotoWidgetState

  @Serializable
  data object LoginRequired : PhotoWidgetState
}
