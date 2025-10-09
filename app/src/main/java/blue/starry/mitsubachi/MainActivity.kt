package blue.starry.mitsubachi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.ui.theme.MyApplicationTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        App()
      }
    }
  }
}

val icons = listOf(
  Icons.Filled.Home,
  Icons.Filled.Place,
)

sealed interface UiState {
  data object Loading : UiState
  data class Success(val feed: List<CheckIn>) : UiState
  data class Error(val message: String) : UiState
}

class FeedViewModel : ViewModel() {
  var uiState: UiState by mutableStateOf(UiState.Loading)
    private set

  val client = SwarmApiClient()

  private fun fetchData() {
    viewModelScope.launch {
      uiState = UiState.Loading

      try {
        val feed = client.getFeed()
        uiState = UiState.Success(feed)
      } catch (e: Exception) {
        uiState = UiState.Error(e.localizedMessage)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
  Scaffold(
    topBar = {
      TopAppBar(title = {
        Text("Swarm")
      })
    },
    bottomBar = {
      var selectedIndex by remember { mutableIntStateOf(0) }

      NavigationBar {
        icons.forEachIndexed { index, icon ->
          NavigationBarItem(
            icon = {
              Icon(imageVector = icon, contentDescription = null)
            },
            label = {
              Text(icon.name)
            },
            onClick = {
              selectedIndex = index
            },
            selected = selectedIndex == index
          )
        }
      }
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        icon = {
          Icon(Icons.Default.Place, contentDescription = null)
        },
        text = {
          Text("Check-in")
        },
        onClick = {}
      )
    },
    floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    // val scope = rememberCoroutineScope()
    val model = remember { FeedViewModel() }
    val listState = rememberLazyListState()

    when (val state = model.uiState) {
      is UiState.Loading -> {

      }

      is UiState.Success -> {
        LazyColumn(state = listState, modifier = Modifier.padding(padding)) {
          for (index in 0 until 100) {
            item { Text(text = "List item - $index", modifier = Modifier.padding(24.dp)) }
          }
        }
      }

      is UiState.Error -> {
        Text(state.message)
      }
    }
  }
}

interface ISwarmApiClient {
  suspend fun getFeed(): List<CheckIn>
}

class SwarmApiClient : ISwarmApiClient {
  val client = HttpClient(OkHttp)

  override suspend fun getFeed(): List<CheckIn> = listOf(
    CheckIn(
      place = Place(
        id = 1,
        name = "皇居",
        address = "Chiyoda, Tokyo",
        meyership = Meyership(
          holder = User(
            id = 2,
            name = "the_emperor",
            displayName = "天皇",
          ),
          checkIns = 30,
        ),
      ),
      user = User(
        id = 3,
        name = "the_retired_emperor",
        displayName = "上皇",
      ),
      coin = 150,
      sticker = null,
      timestamp = ZonedDateTime.now(),
      isLiked = false,
    )
  )
}

data class CheckIn(
  val place: Place,
  val user: User,
  val coin: Int,
  val sticker: String?,
  val timestamp: ZonedDateTime,
  val isLiked: Boolean,
)

data class Place(
  val id: Int,
  val name: String,
  val address: String,
  val meyership: Meyership?,
)

data class User(
  val id: Int,
  val name: String,
  val displayName: String,
)

data class Meyership(
  val holder: User,
  val checkIns: Int,
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme {
    Greeting("Android")
  }
}