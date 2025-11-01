package blue.starry.mitsubachi

import androidx.compose.foundation.layout.padding
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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
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
import blue.starry.mitsubachi.ui.feature.map.MapScreen
import blue.starry.mitsubachi.ui.feature.map.MapScreenTopBar
import blue.starry.mitsubachi.ui.feature.map.histories.VenueHistoriesScreen
import blue.starry.mitsubachi.ui.feature.welcome.WelcomeScreen

@Composable
fun App(viewModel: AppViewModel = hiltViewModel()) {
  val backStack = rememberNavBackStack(RouteKey.Welcome)
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
      modifier = Modifier.padding(padding),
      entryProvider = AppEntryProvider(backStack = backStack),
    )
  }
}

@Composable
private fun AppTopBar(backStack: NavBackStack<NavKey>) {
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
private fun AppBottomBar(backStack: NavBackStack<NavKey>) {
  when (backStack.last()) {
    is RouteKey.Home, is RouteKey.VenueHistories -> {
      HomeScreenBottomBar(
        onClickHome = {
          backStack.add(RouteKey.Home)
        },
        onClickSearch = {},
        onClickMap = {
          backStack.add(RouteKey.VenueHistories)
        },
      )
    }

    else -> {}
  }
}

@Composable
private fun AppFloatingActionButton(backStack: NavBackStack<NavKey>) {
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
private fun AppEntryProvider(backStack: NavBackStack<NavKey>): (NavKey) -> NavEntry<NavKey> {
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

      else -> {
        TODO("route $key not implemented")
      }
    }
  }
}
