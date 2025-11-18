package blue.starry.mitsubachi.core.ui.glance

import android.content.Context
import android.content.res.Configuration
import androidx.glance.unit.ColorProvider

fun ColorProvider.withAlpha(context: Context, alpha: Float): ColorProvider {
  val dayColor = getColor(context.createNightModeContext(false)).copy(alpha = alpha)
  val nightColor = getColor(context.createNightModeContext(true)).copy(alpha = alpha)

  return androidx.glance.color.ColorProvider(day = dayColor, night = nightColor)
}

private fun Context.createNightModeContext(isNightMode: Boolean): Context {
  val configuration = Configuration(resources.configuration)
  val uiModeFlag = when {
    isNightMode -> Configuration.UI_MODE_NIGHT_YES
    else -> Configuration.UI_MODE_NIGHT_NO
  }
  configuration.uiMode =
    uiModeFlag or (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
  return createConfigurationContext(configuration)
}
