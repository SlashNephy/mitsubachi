package blue.starry.mitsubachi.core.ui.glance

import androidx.glance.material3.ColorProviders
import blue.starry.mitsubachi.core.ui.common.MitsubachiColorScheme

object MitsubachiGlanceColorScheme {
  val colors = ColorProviders(
    light = MitsubachiColorScheme.Light,
    dark = MitsubachiColorScheme.Dark,
  )
}
