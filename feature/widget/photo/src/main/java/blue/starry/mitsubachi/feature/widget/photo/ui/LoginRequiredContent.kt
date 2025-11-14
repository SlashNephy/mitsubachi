package blue.starry.mitsubachi.feature.widget.photo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import blue.starry.mitsubachi.feature.widget.photo.R

@Composable
internal fun LoginRequiredContent(modifier: GlanceModifier = GlanceModifier) {
  val context = LocalContext.current

  Column(
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    Text(
      text = context.getString(R.string.login_required_title),
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      ),
    )

    Spacer(modifier = GlanceModifier.height(16.dp))

    Text(
      text = context.getString(R.string.login_required_description),
      style = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 12.sp,
      ),
    )
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun LoginRequiredContentPreview() {
  LoginRequiredContent()
}
