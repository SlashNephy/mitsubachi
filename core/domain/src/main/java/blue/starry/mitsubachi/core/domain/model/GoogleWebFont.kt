package blue.starry.mitsubachi.core.domain.model

data class GoogleWebFont(
  val category: Category,
  val family: String,
  val subsets: List<Subset>,
) {
  sealed interface Category {
    data object Display : Category
    data object SansSerif : Category
    data object Serif : Category
    data object Handwriting : Category
    data object Monospace : Category
    data class Other(val name: String) : Category
  }

  sealed interface Subset {
    data object Latin : Subset
    data object Japanese : Subset
    data object Korean : Subset
    data class Other(val name: String) : Subset
  }
}
