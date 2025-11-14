plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.convention.kotlin.serialization)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.core.common"
}
