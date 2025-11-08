plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.ui.feature.photowidget"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui)

  // Glance for widgets
  implementation(libs.androidx.glance.appwidget)
  implementation(libs.androidx.glance.material3)

  // WorkManager for background tasks
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  ksp(libs.androidx.hilt.compiler)

  // Coil for image loading
  implementation(libs.coil.compose)

  testImplementation(libs.junit.jupiter)
  testImplementation(libs.mockk)
  testImplementation(libs.androidx.work.testing)
}
