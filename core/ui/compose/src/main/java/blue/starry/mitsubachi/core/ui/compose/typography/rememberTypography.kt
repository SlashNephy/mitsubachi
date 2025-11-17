package blue.starry.mitsubachi.core.ui.compose.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.util.Locale

@Composable
fun rememberTypography(): Typography {
  val configuration = LocalConfiguration.current
  return remember(configuration) {
    val locale = ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
    when (locale.language) {
      Locale.JAPANESE.language -> {
        MitsubachiTypography.Japanese
      }

      Locale.KOREAN.language -> {
        MitsubachiTypography.Korean
      }

      else -> {
        MitsubachiTypography.English
      }
    }
  }
}
