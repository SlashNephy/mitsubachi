plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.detekt)
  alias(libs.plugins.convention.hilt)
}

android {
  namespace = "blue.starry.mitsubachi.core.ui.common"
}

dependencies {
  implementation(projects.core.domain)
  api(projects.core.ui.symbols)

  implementation(platform(libs.androidx.compose.bom))
  api(libs.androidx.compose.material3)
}
