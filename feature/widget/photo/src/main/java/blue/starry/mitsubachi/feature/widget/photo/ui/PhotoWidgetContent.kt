package blue.starry.mitsubachi.feature.widget.photo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import blue.starry.mitsubachi.feature.widget.photo.state.PhotoWidgetState

@Composable
internal fun PhotoWidgetContent() {
  Box(
    modifier = GlanceModifier
      .fillMaxSize()
      .background(GlanceTheme.colors.surface)
      .cornerRadius(16.dp),
  ) {
    when (val state = currentState<PhotoWidgetState>()) {
      is PhotoWidgetState.Loading -> LoadingContent()
      is PhotoWidgetState.Photo -> PhotoContent(state)
      is PhotoWidgetState.NoPhotos -> NoPhotosContent()
      is PhotoWidgetState.LoginRequired -> LoginRequiredContent()
    }
  }
}
