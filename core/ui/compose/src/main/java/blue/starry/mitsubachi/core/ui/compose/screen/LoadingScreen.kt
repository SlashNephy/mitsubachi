package blue.starry.mitsubachi.core.ui.compose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(64.dp),
    contentAlignment = Alignment.Center,
  ) {
    LoadingIndicator(modifier = Modifier.scale(1.5f))
  }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingScreenPreview() {
  LoadingScreen()
}
