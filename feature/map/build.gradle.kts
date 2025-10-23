plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.ui.feature.map"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui)

  implementation(libs.play.services.maps)
  implementation(libs.android.maps.ktx)
  implementation(libs.android.maps.compose)
  implementation(libs.android.places)
}
