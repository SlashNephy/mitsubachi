package blue.starry.mitsubachi.core.ui.compose.typography

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle

@Composable
fun OverrideTextStyle(
  style: @Composable (TextStyle) -> TextStyle,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    value = LocalTextStyle provides style(LocalTextStyle.current),
    content = content,
  )
}
