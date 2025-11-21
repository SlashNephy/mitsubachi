package blue.starry.mitsubachi

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import blue.starry.mitsubachi.core.domain.model.ColorSchemePreference
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
      val uiState by viewModel.state.collectAsState()

      when (val state = uiState) {
        is MainActivityViewModel.UiState.Initializing -> {
          // Show nothing or splash screen while initializing
        }

        is MainActivityViewModel.UiState.Ready -> {
          val systemDarkTheme = isSystemInDarkTheme()
          val darkTheme = when (state.applicationSettings.colorSchemePreference) {
            ColorSchemePreference.Light -> false
            ColorSchemePreference.Dark -> true
            ColorSchemePreference.System -> systemDarkTheme
          }

          MitsubachiTheme(
            darkTheme = darkTheme,
            dynamicColor = state.applicationSettings.isDynamicColorEnabled,
            fontFamilyPreference = state.applicationSettings.fontFamilyPreference,
          ) {
            App(initialRouteKeys = viewModel.buildInitialRouteKeys(intent))
          }
        }
      }
    }
  }
}
