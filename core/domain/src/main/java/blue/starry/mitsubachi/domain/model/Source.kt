package blue.starry.mitsubachi.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Source(
  val name: String,
  val url: String?,
)
