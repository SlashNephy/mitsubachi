package blue.starry.mitsubachi.ui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

val navigationBarItems = listOf(
  Icons.Filled.Home to R.string.home_bar,
  Icons.Filled.Place to R.string.search_bar,
)

@Composable
fun HomeScreenBottomBar() {
  var selectedIndex by remember { mutableIntStateOf(0) }

  NavigationBar {
    navigationBarItems.forEachIndexed { index, (icon, labelId) ->
      NavigationBarItem(
        icon = {
          Icon(
            imageVector = icon,
            contentDescription = stringResource(labelId),
          )
        },
        label = {
          Text(stringResource(labelId))
        },
        onClick = {
          selectedIndex = index
        },
        selected = selectedIndex == index,
      )
    }
  }
}
