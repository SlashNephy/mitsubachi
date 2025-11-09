plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
  alias(libs.plugins.convention.kotlin.serialization)
}

android {
  namespace = "blue.starry.mitsubachi.feature.photowidget.ui"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui)

  implementation(libs.androidx.glance.appwidget)
  implementation(libs.androidx.glance.material3)
  implementation(libs.androidx.glance.preview)
  debugImplementation(libs.androidx.glance.appwidget.preview)
  testImplementation(libs.androidx.glance.testing)
  testImplementation(libs.androidx.glance.appwidget.testing)

  ksp(libs.androidx.hilt.compiler)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  testImplementation(libs.androidx.work.testing)

  implementation(libs.coil.compose)

  implementation(libs.androidx.datastore)
}
