package blue.starry.mitsubachi.core.domain.model

import kotlinx.serialization.Serializable

data class GoogleWebFont(
  val category: Category,
  val family: String,
  val subsets: List<Subset>,
) {
  enum class Category {
    Display,
    SansSerif,
    Serif,
    Handwriting,
    Monospace,
  }

  @Serializable
  sealed interface Subset {
    data object Latin : Subset
    data object Japanese : Subset
    data object Korean : Subset
    data class Other(val name: String) : Subset
  }
}
