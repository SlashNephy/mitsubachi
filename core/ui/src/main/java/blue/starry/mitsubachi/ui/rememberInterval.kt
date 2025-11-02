package blue.starry.mitsubachi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.time.Duration

@Composable
fun <T> rememberInterval(interval: Duration, block: () -> T): T {
  var value by remember { mutableStateOf(block()) }
  LaunchedEffect(block) {
    while (true) {
      delay(interval)
      value = block()
    }
  }

  return value
}
