plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.testing"
}

dependencies {
  api(kotlin("test"))

  api(libs.junit)
  api(libs.junit.jupiter)
  api(libs.mockk)
  api(libs.archunit)
}
