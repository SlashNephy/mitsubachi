plugins {
  alias(libs.plugins.convention.android.glance.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
  alias(libs.plugins.convention.kotlin.serialization)
}

android {
  namespace = "blue.starry.mitsubachi.feature.widget.photo"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui.common)
  implementation(projects.core.ui.glance)

  ksp(libs.androidx.hilt.compiler)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  testImplementation(libs.androidx.work.testing)
}
