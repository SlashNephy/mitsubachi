package blue.starry.mitsubachi.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Source(
  val name: String,
  val url: String?,
)
