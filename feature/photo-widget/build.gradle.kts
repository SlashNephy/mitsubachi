plugins {
  alias(libs.plugins.convention.android.glance.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
  alias(libs.plugins.convention.kotlin.serialization)
}

android {
  namespace = "blue.starry.mitsubachi.feature.photowidget"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.uiWidget)

  ksp(libs.androidx.hilt.compiler)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  testImplementation(libs.androidx.work.testing)
}
