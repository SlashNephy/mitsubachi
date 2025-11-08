package blue.starry.mitsubachi.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun <T> LocaleAware(block: @Composable () -> T): T {
  if (LocalInspectionMode.current) {
    return block()
  }

  return LocaleAwareActual(block = block)
}

@Composable
private fun <T> LocaleAwareActual(
  viewModel: LocaleAwareViewModel = hiltViewModel(),
  block: @Composable () -> T,
): T {
  val version = remember { mutableIntStateOf(0) }
  LaunchedEffect(viewModel) {
    viewModel.onLocaleChanged.collect {
      version.intValue++
    }
  }

  return key(version.intValue) {
    block()
  }
}
