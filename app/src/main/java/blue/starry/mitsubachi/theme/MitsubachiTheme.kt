package blue.starry.mitsubachi.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import blue.starry.mitsubachi.ui.color.MitsubachiColorScheme

@Composable
fun MitsubachiTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> {
      MitsubachiColorScheme.Dark
    }

    else -> {
      MitsubachiColorScheme.Light
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = rememberTypography(),
    content = content,
  )
}
