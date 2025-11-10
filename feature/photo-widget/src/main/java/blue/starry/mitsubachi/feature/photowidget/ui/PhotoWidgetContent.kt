package blue.starry.mitsubachi.feature.photowidget.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import blue.starry.mitsubachi.feature.photowidget.state.PhotoWidgetState

@Composable
internal fun PhotoWidgetContent() {
  Scaffold(
    modifier = GlanceModifier
      .fillMaxSize(),
  ) {
    when (val state = currentState<PhotoWidgetState>()) {
      is PhotoWidgetState.Loading -> LoadingContent()
      is PhotoWidgetState.Photo -> PhotoContent(state)
      is PhotoWidgetState.NoPhotos -> NoPhotosContent()
      is PhotoWidgetState.LoginRequired -> LoginRequiredContent()
    }
  }
}
