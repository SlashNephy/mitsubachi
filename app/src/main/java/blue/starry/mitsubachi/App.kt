package blue.starry.mitsubachi

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import blue.starry.mitsubachi.feature.settings.SettingsScreen
import blue.starry.mitsubachi.ui.feature.checkin.CheckInDetailScreen
import blue.starry.mitsubachi.ui.feature.checkin.CheckInDetailScreenTopBar
import blue.starry.mitsubachi.ui.feature.checkin.CreateCheckInScreen
import blue.starry.mitsubachi.ui.feature.checkin.CreateCheckInScreenTopBar
import blue.starry.mitsubachi.ui.feature.checkin.NearbyVenuesScreen
import blue.starry.mitsubachi.ui.feature.checkin.NearbyVenuesScreenTopBar
import blue.starry.mitsubachi.ui.feature.home.HomeScreen
import blue.starry.mitsubachi.ui.feature.home.HomeScreenBottomBar
import blue.starry.mitsubachi.ui.feature.home.HomeScreenFloatingActionButton
import blue.starry.mitsubachi.ui.feature.home.HomeScreenTopBar
import blue.starry.mitsubachi.ui.feature.home.UserCheckInsScreen
import blue.starry.mitsubachi.ui.feature.map.MapScreen
import blue.starry.mitsubachi.ui.feature.map.MapScreenTopBar
import blue.starry.mitsubachi.ui.feature.map.histories.VenueHistoriesScreen
import blue.starry.mitsubachi.ui.feature.map.search.SearchMapScreen
import blue.starry.mitsubachi.ui.feature.welcome.WelcomeScreen
import blue.starry.mitsubachi.ui.navigation.rememberNavBackStack

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App(viewModel: AppViewModel = hiltViewModel()) {
  val backStack = rememberNavBackStack<RouteKey>(RouteKey.Welcome)
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel, snackbarHostState) {
    viewModel.snackbarMessages.collect { message ->
      snackbarHostState.showSnackbar(message = message.text)
    }
  }

  Scaffold(
    topBar = { AppTopBar(backStack = backStack) },
    bottomBar = { AppBottomBar(backStack = backStack) },
    floatingActionButton = { AppFloatingActionButton(backStack = backStack) },
    floatingActionButtonPosition = AppFloatingActionButtonPosition(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { padding ->
    NavDisplay(
      backStack = backStack,
      modifier = Modifier.padding(scaffoldPadding(padding, backStack)),
      entryProvider = AppEntryProvider(backStack = backStack),
    )
  }
}

private fun scaffoldPadding(
  padding: PaddingValues,
  backStack: NavBackStack<RouteKey>,
): PaddingValues {
  return when (backStack.last()) {
    is RouteKey.Search, is RouteKey.VenueHistories -> {
      // ステータスバー (top) の padding を除外する
      PaddingValues(
        bottom = padding.calculateBottomPadding(),
      )
    }

    else -> {
      padding
    }
  }
}

@Composable
private fun AppTopBar(backStack: NavBackStack<RouteKey>) {
  when (val key = backStack.last()) {
    is RouteKey.Home -> {
      HomeScreenTopBar(onClickSettingsButton = {
        backStack.add(RouteKey.Settings)
      })
    }

    is RouteKey.NearbyVenues -> {
      NearbyVenuesScreenTopBar(
        onBack = {
          backStack.remove(key)
        },
      )
    }

    is RouteKey.CreateCheckIn -> {
      CreateCheckInScreenTopBar(
        venue = key.venue,
        onBack = {
          backStack.remove(key)
        },
      )
    }

    is RouteKey.CheckInDetail -> {
      CheckInDetailScreenTopBar(
        checkIn = key.checkIn,
        onBack = {
          backStack.remove(key)
        },
      )
    }

    is RouteKey.Map -> {
      MapScreenTopBar(
        onBack = {
          backStack.remove(key)
        },
      )
    }

    else -> {}
  }
}

@Composable
private fun AppBottomBar(backStack: NavBackStack<RouteKey>) {
  when (backStack.last()) {
    is RouteKey.Home, is RouteKey.Search, is RouteKey.VenueHistories, is RouteKey.UserCheckIns -> {
      HomeScreenBottomBar(
        onClickHome = {
          backStack.add(RouteKey.Home)
        },
        onClickSearch = {
          backStack.add(RouteKey.Search)
        },
        onClickMap = {
          backStack.add(RouteKey.VenueHistories)
        },
        onClickUserCheckIns = {
          backStack.add(RouteKey.UserCheckIns)
        },
      )
    }

    else -> {}
  }
}

@Composable
private fun AppFloatingActionButton(backStack: NavBackStack<RouteKey>) {
  when (backStack.last()) {
    is RouteKey.Home -> {
      HomeScreenFloatingActionButton(
        onClickCheckInButton = {
          backStack.add(RouteKey.NearbyVenues)
        },
      )
    }

    else -> {}
  }
}

@Suppress("FunctionName")
private fun AppFloatingActionButtonPosition(): FabPosition {
  return FabPosition.End
}

@Suppress("FunctionName", "LongMethod")
private fun AppEntryProvider(backStack: NavBackStack<RouteKey>): (RouteKey) -> NavEntry<RouteKey> {
  return { key ->
    when (key) {
      is RouteKey.Welcome -> {
        NavEntry(key) {
          WelcomeScreen(
            onLogin = {
              backStack.remove(RouteKey.Welcome) // 戻れないようにする
              backStack.add(RouteKey.Home)
            },
          )
        }
      }

      is RouteKey.Home -> {
        NavEntry(key) {
          HomeScreen(
            onClickCheckIn = { checkIn ->
              backStack.add(RouteKey.CheckInDetail(checkIn))
            },
          )
        }
      }

      is RouteKey.NearbyVenues -> {
        NavEntry(key) {
          NearbyVenuesScreen(
            onClickVenue = { venue ->
              backStack.add(RouteKey.CreateCheckIn(venue))
            },
          )
        }
      }

      is RouteKey.CreateCheckIn -> {
        NavEntry(key) {
          CreateCheckInScreen(
            venue = key.venue,
            onCompleteCheckIn = {
              backStack.clear()
              backStack.add(RouteKey.Home)
            },
            onCancelCheckIn = {
              backStack.remove(key)
            },
          )
        }
      }

      is RouteKey.CheckInDetail -> {
        NavEntry(key) {
          CheckInDetailScreen(key.checkIn)
        }
      }

      is RouteKey.User -> {
        error("TODO: not implemented")
      }

      is RouteKey.Map -> {
        NavEntry(key) {
          MapScreen(
            latitude = key.latitude,
            longitude = key.longitude,
            title = key.title,
          )
        }
      }

      is RouteKey.VenueHistories -> {
        NavEntry(key) {
          VenueHistoriesScreen()
        }
      }

      is RouteKey.Search -> {
        NavEntry(key) {
          SearchMapScreen()
        }
      }

      is RouteKey.Settings -> {
        NavEntry(key) {
          SettingsScreen(
            onSignOut = {
              backStack.clear()
              backStack.add(RouteKey.Welcome)
            },
          )
        }
      }

      is RouteKey.UserCheckIns -> {
        NavEntry(key) {
          UserCheckInsScreen(
            onClickCheckIn = { checkIn ->
              backStack.add(RouteKey.CheckInDetail(checkIn))
            },
          )
        }
      }
    }
  }
}
