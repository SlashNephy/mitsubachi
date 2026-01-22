plugins {
  alias(libs.plugins.convention.android.compose.library)
  alias(libs.plugins.convention.kotlin.serialization)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
  alias(libs.plugins.kover)
}

android {
  namespace = "blue.starry.mitsubachi.core.ui.compose"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.ui.common)

  api(libs.androidx.core.ktx)
  api(libs.androidx.activity.compose)

  // androidx.compose
  implementation(platform(libs.androidx.compose.bom))
  api(libs.androidx.compose.ui)
  api(libs.androidx.compose.ui.graphics)
  api(libs.androidx.compose.ui.text.google.fonts)
  api(libs.androidx.compose.material3)

  // androidx.navigation3
  api(libs.androidx.navigation3.runtime)
  api(libs.androidx.navigation3.ui)

  // androidx.lifecycle
  api(libs.androidx.lifecycle.runtime.ktx)
  api(libs.androidx.lifecycle.runtime.compose)
  testImplementation(libs.androidx.lifecycle.runtime.testing)
  api(libs.androidx.lifecycle.viewmodel.ktx)
  api(libs.androidx.lifecycle.viewmodel.compose)
  api(libs.androidx.lifecycle.viewmodel.navigation3)

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.crashlytics)

  api(libs.androidx.browser)
  api(libs.accompanist.permissions)

  // Coil
  api(libs.coil.compose)
  implementation(libs.coil.network.ktor3)
  implementation(libs.coil.network.cache.control)
  testImplementation(libs.coil.test)

  api(libs.advanced.bottomsheet.material3)
  implementation(libs.indriya)
}
