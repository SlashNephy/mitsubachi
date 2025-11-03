package blue.starry.mitsubachi.ui.locale

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
@SuppressLint("ComposableNaming")
fun <T> LocaleAware(
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
