package blue.starry.mitsubachi.ui.feature.photowidget

import blue.starry.mitsubachi.common.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

/**
 * State data for the photo widget
 */
@Serializable
data class PhotoWidgetState(
  val photoId: String? = null,
  val checkInId: String? = null,
  val venueName: String? = null,
  @Serializable(with = ZonedDateTimeSerializer::class)
  val checkInDate: ZonedDateTime? = null,
  val isLoggedIn: Boolean = false,
  val hasPhoto: Boolean = false,
)
