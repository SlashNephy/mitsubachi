package blue.starry.mitsubachi.ui.feature.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

private enum class NavigationItem(@param:StringRes val labelId: Int, val icon: ImageVector) {
  Home(R.string.home_bar, Icons.Filled.Home),
  Search(R.string.search_bar, Icons.Filled.Search),
  Map(R.string.map_bar, Icons.Filled.Map),
}

@Composable
fun HomeScreenBottomBar(
  onClickHome: () -> Unit,
  onClickSearch: () -> Unit,
  onClickMap: () -> Unit,
) {
  var selectedItem by remember { mutableStateOf(NavigationItem.Home) }

  NavigationBar {
    for (item in NavigationItem.entries) {
      NavigationBarItem(
        icon = {
          Icon(
            imageVector = item.icon,
            contentDescription = stringResource(item.labelId),
          )
        },
        label = {
          Text(stringResource(item.labelId))
        },
        onClick = {
          selectedItem = item

          when (item) {
            NavigationItem.Home -> onClickHome()
            NavigationItem.Search -> onClickSearch()
            NavigationItem.Map -> onClickMap()
          }
        },
        selected = selectedItem == item,
      )
    }
  }
}
