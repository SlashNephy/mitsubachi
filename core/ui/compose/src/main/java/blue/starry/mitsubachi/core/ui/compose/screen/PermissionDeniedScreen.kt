package blue.starry.mitsubachi.core.ui.compose.screen

import android.content.Intent
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.compose.permission.AndroidPermission
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols

@Composable
fun PermissionDeniedScreen(deniedPermission: AndroidPermission) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(48.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      val (iconId, stringResourceId) = rememberResource(deniedPermission)

      Icon(
        painter = painterResource(iconId),
        contentDescription = null,
        modifier = Modifier.scale(1.5f),
      )

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        stringResource(stringResourceId),
        textAlign = TextAlign.Center,
        style = TextStyle.Default.copy(lineBreak = LineBreak.Paragraph),
      )

      Spacer(modifier = Modifier.height(16.dp))

      OpenSettingsButton()
    }
  }
}

private data class PermissionResource(
  @param:DrawableRes val iconId: Int,
  @param:StringRes val stringResourceId: Int,
)

@Composable
private fun rememberResource(permission: AndroidPermission): PermissionResource {
  return remember(permission) {
    when (permission) {
      is AndroidPermission.Location -> {
        PermissionResource(MaterialSymbols.location_off, R.string.location_permission_missing)
      }
    }
  }
}

@Composable
private fun OpenSettingsButton(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val intent = remember {
    Intent(
      Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
      "package:${context.packageName}".toUri(),
    )
  }

  Button(
    onClick = {
      context.startActivity(intent)
    },
    modifier = modifier,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        painterResource(MaterialSymbols.refresh),
        contentDescription = null,
      )
      Text(stringResource(R.string.open_device_settings_button))
    }
  }
}

@Preview(showSystemUi = true)
@Composable
private fun PermissionDeniedScreenPreview() {
  PermissionDeniedScreen(AndroidPermission.Location)
}

@Preview
@Composable
private fun OpenSettingsButtonPreview() {
  OpenSettingsButton()
}
