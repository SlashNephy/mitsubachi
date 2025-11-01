package blue.starry.mitsubachi

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountEvent
import blue.starry.mitsubachi.domain.usecase.FoursquareAccountRepository
import blue.starry.mitsubachi.theme.MitsubachiTheme
import blue.starry.mitsubachi.ui.AccountEventHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val viewModel: MainActivityViewModel by viewModels()

  @Suppress("LateinitUsage")
  @Inject
  lateinit var accountRepository: FoursquareAccountRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    // TODO: スプラッシュスクリーンの内容をカスタマイズ
    val splashScreen = installSplashScreen()
    splashScreen.setKeepOnScreenCondition {
      viewModel.state.value is MainActivityViewModel.UiState.Initializing
    }

    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.auto(
        Color.TRANSPARENT,
        Color.TRANSPARENT,
      ),
      navigationBarStyle = SystemBarStyle.auto(
        Color.TRANSPARENT,
        Color.TRANSPARENT,
      ),
    )

    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      accountRepository.events.collect {
        propagateAccountEvent(it)
      }
    }

    setContent {
      LaunchedEffect(viewModel) {
        viewModel.onReady()
      }

      MitsubachiTheme {
        App()
      }
    }
  }

  @SuppressLint("RestrictedApi")
  private fun propagateAccountEvent(event: FoursquareAccountEvent) {
    viewModelStore
      .keys()
      .asSequence()
      .mapNotNull { viewModelStore[it] }
      .filterIsInstance<AccountEventHandler>()
      .forEach {
        when (event) {
          is FoursquareAccountEvent.Deleted -> {
            it.onAccountDeleted()
          }

          else -> {}
        }
      }
  }
}
