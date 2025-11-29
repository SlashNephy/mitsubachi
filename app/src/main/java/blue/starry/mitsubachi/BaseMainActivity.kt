package blue.starry.mitsubachi

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blue.starry.mitsubachi.core.domain.model.ColorSchemePreference

abstract class BaseMainActivity : ComponentActivity() {
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
      val state by viewModel.state.collectAsStateWithLifecycle()
      when (val state = state) {
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
            val routeKeys = remember(intent) { viewModel.buildInitialRouteKeys(intent) }
            App(initialRouteKeys = routeKeys)
          }
        }
      }
    }
  }
}
