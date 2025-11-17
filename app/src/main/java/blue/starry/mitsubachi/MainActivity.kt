package blue.starry.mitsubachi

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val viewModel: MainActivityViewModel by viewModels()

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

    setContent {
      LaunchedEffect(viewModel) {
        viewModel.onReady()
      }

      MitsubachiTheme {
        App(initialRouteKeys = viewModel.buildInitialRouteKeys(intent))
      }
    }
  }
}
