package blue.starry.mitsubachi.feature.settings.ui.section

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings

private const val REQUIRED_CLICK_COUNT = 5

@Composable
internal fun VersionSection(
  applicationSettings: ApplicationSettings,
  applicationConfig: ApplicationConfig,
  onChangeApplicationSettings: ((ApplicationSettings) -> ApplicationSettings) -> Unit,
) {
  val context = LocalContext.current
  var clickCount by remember { mutableIntStateOf(0) }

  Row(
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        if (applicationSettings.isAdvancedSettingsAvailable) {
          Toast.makeText(context, "すでに高度な設定は有効です。", Toast.LENGTH_SHORT).show()
        } else {
          if (clickCount++ >= REQUIRED_CLICK_COUNT) {
            onChangeApplicationSettings { settings ->
              settings.copy(
                isAdvancedSettingsAvailable = true,
              )
            }
          }
        }
      },
  ) {
    Text("バージョン: ${applicationConfig.versionName} (${applicationConfig.versionCode})")
  }
}
