package blue.starry.mitsubachi.feature.home.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.home.R

private enum class NavigationItem(@param:StringRes val labelId: Int, @param:DrawableRes val iconRes: Int) {
  Home(R.string.home_bar, MaterialSymbols.home),
  Search(R.string.search_bar, MaterialSymbols.search),
  Map(R.string.map_bar, MaterialSymbols.map),
  UserCheckIns(R.string.user_checkins_bar, MaterialSymbols.person),
}

@Composable
fun HomeScreenBottomBar(
  onClickHome: () -> Unit,
  onClickSearch: () -> Unit,
  onClickMap: () -> Unit,
  onClickUserCheckIns: () -> Unit,
) {
  var selectedItem by remember { mutableStateOf(NavigationItem.Home) }

  NavigationBar {
    for (item in NavigationItem.entries) {
      NavigationBarItem(
        icon = {
          Icon(
            painter = painterResource(item.iconRes),
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
            NavigationItem.UserCheckIns -> onClickUserCheckIns()
          }
        },
        selected = selectedItem == item,
      )
    }
  }
}
