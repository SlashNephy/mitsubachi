plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.ui.feature.home"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui)
}
