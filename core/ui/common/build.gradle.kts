plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.core.ui.common"
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  api(libs.androidx.compose.material3)
}
