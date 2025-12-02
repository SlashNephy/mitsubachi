package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.model.GoogleWebFont
import kotlinx.serialization.Serializable

@Serializable
data class GoogleWebFontListResponse(val items: List<Item>) {
  @Serializable
  data class Item(
    val category: String,
    val family: String,
    val subsets: List<String>,
  )
}

internal fun GoogleWebFontListResponse.toDomain(): List<GoogleWebFont> {
  return items.map(GoogleWebFontListResponse.Item::toDomain)
}

private fun GoogleWebFontListResponse.Item.toDomain(): GoogleWebFont {
  return GoogleWebFont(
    category = parseCategory(category),
    family = family,
    subsets = subsets.map(::parseSubset),
  )
}

private fun parseCategory(value: String): GoogleWebFont.Category {
  return when (value) {
    "display" -> GoogleWebFont.Category.Display
    "sans-serif" -> GoogleWebFont.Category.SansSerif
    "serif" -> GoogleWebFont.Category.Serif
    "handwriting" -> GoogleWebFont.Category.Handwriting
    "monospace" -> GoogleWebFont.Category.Monospace
    else -> GoogleWebFont.Category.Other(value)
  }
}

private fun parseSubset(value: String): GoogleWebFont.Subset {
  return when (value) {
    "latin" -> GoogleWebFont.Subset.Latin
    "japanese" -> GoogleWebFont.Subset.Japanese
    "korean" -> GoogleWebFont.Subset.Korean
    else -> GoogleWebFont.Subset.Other(value)
  }
}
