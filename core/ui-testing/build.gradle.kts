plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.ui.testing"
}

dependencies {
  api(kotlin("test"))

  api(libs.androidx.junit)
  api(libs.androidx.espresso.core)
  api(libs.androidx.compose.ui.test.junit4)
}
