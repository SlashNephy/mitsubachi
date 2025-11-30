plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.feature.appfunctions"
}

ksp {
  arg("appfunctions:aggregateAppFunctions", "true")
  // arg("appfunctions:generateMetadataFromSchema", "true")
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)

  implementation(libs.androidx.appfunctions)
  implementation(libs.androidx.appfunctions.service)
  ksp(libs.androidx.appfunctions.compiler)
}
