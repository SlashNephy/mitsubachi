import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.io.FileInputStream
import java.util.Properties

plugins {
  alias(libs.plugins.convention.android.compose.application)
  alias(libs.plugins.google.services)
  alias(libs.plugins.firebase.crashlytics)
  alias(libs.plugins.firebase.app.distribution)
  alias(libs.plugins.convention.kotlin.serialization)
  alias(libs.plugins.convention.hilt)
  alias(libs.plugins.convention.detekt)

  alias(libs.plugins.android.mapsplatform.secrets)
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
    applicationId = "blue.starry.mitsubachi"

    versionName = project.findProperty("versionName") as? String ?: "1.0"
    versionCode = (project.findProperty("versionCode") as? String)?.toIntOrNull() ?: 1

    buildConfigField("String", "NAMESPACE", "\"${namespace}\"")
  }

  firebaseAppDistributionDefault {
    artifactType = "APK" // Play Store のデベロッパーアカウントを作ったら AAB にする
    serviceCredentialsFile = "$rootDir/firebase-service-account.json"
  }

  signingConfigs {
    create("default") {
      storeFile = localProperties.getProperty("android_keystore_path")?.let { file(it) }
      storePassword = localProperties.getProperty("android_keystore_password")
      keyAlias = localProperties.getProperty("android_keystore_alias")
      keyPassword = localProperties.getProperty("android_keystore_alias_password")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      configure<CrashlyticsExtension> {
        mappingFileUploadEnabled = true
        nativeSymbolUploadEnabled = true
      }
    }
    debug {
      isDebuggable = true
    }
  }

  productFlavors {
    flavorDimensions("environment")
    create("production") {
      dimension = "environment"
      signingConfig = signingConfigs.getByName("default")
    }
    create("staging") {
      dimension = "environment"
      applicationIdSuffix = ".staging"
      signingConfig = signingConfigs.getByName("default")
      firebaseAppDistribution {
        groups = "tester"
      }
    }
    create("local") {
      dimension = "environment"
      applicationIdSuffix = ".local"
    }
  }

  androidResources {
    @Suppress("UnstableApiUsage")
    generateLocaleConfig = true
  }
}

secrets {
  propertiesFileName = rootProject.relativePath("local.properties")
  defaultPropertiesFileName = rootProject.relativePath("local.defaults.properties")
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.data)
  implementation(projects.core.domain)
  implementation(projects.core.ui.common)
  implementation(projects.core.ui.compose)
  implementation(projects.feature.checkin)
  implementation(projects.feature.home)
  implementation(projects.feature.map)
  implementation(projects.feature.settings)
  implementation(projects.feature.welcome)
  implementation(projects.feature.widget.photo)

  implementation(libs.androidx.core.splashscreen)

  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)

  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.crashlytics)
  implementation(libs.firebase.crashlytics.ndk)
  implementation(libs.firebase.analytics)
}
