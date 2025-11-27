plugins {
  alias(libs.plugins.convention.android.wear.compose.library)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.core.ui.wear"
}

dependencies {
  // androidx.wear.compose
  api(libs.androidx.wear.compose.foundation)
  api(libs.androidx.wear.compose.material3)
  api(libs.androidx.wear.compose.navigation)
}
