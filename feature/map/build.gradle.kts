plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.feature.map"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui.common)
  implementation(projects.core.ui.compose)

  implementation(libs.play.services.maps)
  implementation(libs.android.maps.ktx)
  implementation(libs.android.maps.utils.ktx)
  implementation(libs.android.maps.compose)
  implementation(libs.android.maps.compose.utils)
  implementation(libs.android.places)
}
