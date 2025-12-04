plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.convention.kotlin.serialization)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.core.domain"
}

dependencies {
  implementation(projects.core.common)

  api(libs.kotlinx.collections.immutable)

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.runtime.annotation)
}
