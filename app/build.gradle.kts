import java.io.FileInputStream
import java.util.Properties

plugins {
  alias(libs.plugins.convention.android.compose.application)
  alias(libs.plugins.convention.kotlin.serialization)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
  FileInputStream(localPropertiesFile).use {
    localProperties.load(it)
  }
}

android {
  namespace = "blue.starry.mitsubachi"

  defaultConfig {
    versionCode = 1
    versionName = "1.0"
    applicationId = "blue.starry.mitsubachi"

    buildConfigField("String", "NAMESPACE", "\"${namespace}\"")
    buildConfigField(
      "String",
      "FOURSQUARE_CLIENT_ID",
      localProperties.getProperty("foursquare.client_id", "\"dummy\"")
    )
    // クライアントシークレットは製品アプリでバンドルすべきではないがストア公開しないので諦める
    buildConfigField(
      "String",
      "FOURSQUARE_CLIENT_SECRET",
      localProperties.getProperty("foursquare.client_secret", "\"dummy\"")
    )
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
    debug {
      applicationIdSuffix = ".debug"
      isDebuggable = true
    }
  }

  androidResources {
    @Suppress("UnstableApiUsage")
    generateLocaleConfig = true
  }
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.data)
  implementation(projects.core.domain)
  implementation(projects.core.ui)
  implementation(projects.feature.checkin)
  implementation(projects.feature.home)
  implementation(projects.feature.settings)
  implementation(projects.feature.welcome)

  implementation(libs.appauth)
  implementation(libs.androidx.core.splashscreen)

  debugImplementation(libs.slf4j.android)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  testImplementation(projects.core.testing)
  testRuntimeOnly(libs.junit.jupiter.engine)
  testRuntimeOnly(libs.junit.platform.launcher)
  androidTestImplementation(projects.core.uiTesting)
}
