plugins {
  alias(libs.plugins.convention.android.glance.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
  alias(libs.plugins.convention.kotlin.serialization)
}

android {
  namespace = "blue.starry.mitsubachi.core.ui.widget"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.uiColor)
}
