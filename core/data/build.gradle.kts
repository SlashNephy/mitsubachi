plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.convention.kotlin.serialization)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.datastore)
  alias(libs.plugins.convention.room)
  alias(libs.plugins.convention.ktorfit)
  alias(libs.plugins.convention.detekt)
}

android {
  namespace = "blue.starry.mitsubachi.core.data"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.domain)

  // Room
  implementation(libs.sqlcipher.android) {
    artifact {
      type = "aar"
    }
  }
  implementation(libs.androidx.sqlite)

  // Google Play Services
  implementation(libs.play.services.location)
  implementation(libs.kotlinx.coroutines.play.services)

  // Ktor
  implementation(platform(libs.ktor.bom))
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.okhttp)
  implementation(libs.ktor.client.content.negotiation)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.client.auth)
  debugImplementation(libs.ktor.client.logging)

  implementation(libs.cache4k)
  implementation(libs.apache.commons.lang)
}
