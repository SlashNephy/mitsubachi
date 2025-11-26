package blue.starry.mitsubachi.feature.map.ui.search

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.usecase.VenueRecommendationSection
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.map.R

private data class Section(
  val section: VenueRecommendationSection,
  @param:DrawableRes val icon: Int,
  @param:StringRes val label: Int,
)

private val sections = listOf(
  Section(
    section = VenueRecommendationSection.Food,
    icon = MaterialSymbols.fork_spoon,
    label = R.string.search_map_category_food,
  ),
  Section(
    section = VenueRecommendationSection.Coffee,
    icon = MaterialSymbols.coffee,
    label = R.string.search_map_category_coffee,
  ),
  Section(
    section = VenueRecommendationSection.Shops,
    icon = MaterialSymbols.shopping_bag,
    label = R.string.search_map_category_shops,
  ),
  Section(
    section = VenueRecommendationSection.Sights,
    icon = MaterialSymbols.attractions,
    label = R.string.search_map_category_sights,
  ),
  Section(
    section = VenueRecommendationSection.Arts,
    icon = MaterialSymbols.palette,
    label = R.string.search_map_category_arts,
  ),
)

@Composable
internal fun SectionChipRow(
  selectedSection: VenueRecommendationSection?,
  onSelect: (VenueRecommendationSection?) -> Unit,
  contentPadding: PaddingValues,
) {
  LazyRow(
    contentPadding = contentPadding,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(sections, key = { it.section }) { (section, icon, label) ->
      SectionChip(
        selected = selectedSection == section,
        onClick = {
          onSelect(
            if (selectedSection == section) {
              null
            } else {
              section
            },
          )
        },
        icon = icon,
        label = label
      )
    }
  }
}

@Composable
private fun SectionChip(
  selected: Boolean,
  onClick: () -> Unit,
  @DrawableRes icon: Int,
  @StringRes label: Int,
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    leadingIcon = {
      Icon(
        imageVector = ImageVector.vectorResource(icon),
        contentDescription = null,
        modifier = Modifier.scale(0.8f),
      )
    },
    label = {
      Text(stringResource(label))
    },
    colors = FilterChipDefaults.filterChipColors(
      containerColor = MaterialTheme.colorScheme.surface,
      labelColor = MaterialTheme.colorScheme.onSurface,
    ),
    shape = ShapeDefaults.Medium,
  )
}

@Preview
@Composable
private fun SectionChipRowPreview() {
  SectionChipRow(
    selectedSection = VenueRecommendationSection.Food,
    onSelect = {},
    contentPadding = PaddingValues(0.dp),
  )
}
