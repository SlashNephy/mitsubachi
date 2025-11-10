package blue.starry.mitsubachi.feature.photowidget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
internal fun NoPhotosContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalAlignment = Alignment.CenterVertically,
    modifier = GlanceModifier.padding(16.dp),
  ) {
    Text(
      text = "No photos yet",
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )
    Spacer(modifier = GlanceModifier.height(8.dp))
    Text(
      text = "Add photos to your check-ins to see them here",
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}
