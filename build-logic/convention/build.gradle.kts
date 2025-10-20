plugins {
  `kotlin-dsl`
}

group = "blue.starry.mitsubachi.buildlogic.convention"

dependencies {
  compileOnly(libs.android.application.gradle.plugin)
  compileOnly(libs.kotlin.android.gradle.plugin)
  compileOnly(libs.kotlin.compose.gradle.plugin)
  compileOnly(libs.kotlin.serialization.gradle.plugin)
  compileOnly(libs.ksp.gradle.plugin)
  compileOnly(libs.hilt.gradle.plugin)
  compileOnly(libs.protobuf.gradle.plugin)
  compileOnly(libs.room.gradle.plugin)
  compileOnly(libs.detekt.gradle.plugin)
}

tasks {
  validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
  }
}

gradlePlugin {
  plugins {
    register("android-compose-application") {
      id = libs.plugins.convention.android.compose.application.get().pluginId
      implementationClass = "AndroidComposeApplicationConventionPlugin"
    }
    register("android-library") {
      id = libs.plugins.convention.android.library.get().pluginId
      implementationClass = "AndroidLibraryConventionPlugin"
    }
    register("android-compose-library") {
      id = libs.plugins.convention.android.compose.library.get().pluginId
      implementationClass = "AndroidComposeLibraryConventionPlugin"
    }
    register("detekt") {
      id = libs.plugins.convention.detekt.get().pluginId
      implementationClass = "DetektConventionPlugin"
    }
    register("hilt") {
      id = libs.plugins.convention.hilt.get().pluginId
      implementationClass = "HiltConventionPlugin"
    }
    register("kotlin-serialization") {
      id = libs.plugins.convention.kotlin.serialization.get().pluginId
      implementationClass = "KotlinSerializationConventionPlugin"
    }
    register("datastore") {
      id = libs.plugins.convention.datastore.get().pluginId
      implementationClass = "DataStoreConventionPlugin"
    }
    register("room") {
      id = libs.plugins.convention.room.get().pluginId
      implementationClass = "RoomConventionPlugin"
    }
  }
}
