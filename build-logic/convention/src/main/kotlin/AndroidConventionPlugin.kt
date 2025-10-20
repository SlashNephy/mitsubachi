import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

open class AndroidBaseConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("kotlin-android"))
      }

      tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_21)
        }
      }
    }
  }

  fun CommonExtension<*, *, *, *, *, *>.applyCommonConfiguration() {
    compileSdk = 36

    defaultConfig {
      minSdk = 36
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
      buildConfig = true
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_21
      targetCompatibility = JavaVersion.VERSION_21
    }

    lint {
      checkDependencies = true
    }

    testOptions.unitTests.isIncludeAndroidResources = true
  }

  fun ApplicationExtension.applyApplicationConfiguration() {
    defaultConfig {
      targetSdk = 36
    }
  }

  fun CommonExtension<*, *, *, *, *, *>.enableCompose() {
    buildFeatures {
      compose = true
    }
  }
}

open class AndroidLibraryConventionPlugin : AndroidBaseConventionPlugin() {
  override fun apply(target: Project) {
    super.apply(target)

    with(target) {
      with(pluginManager) {
        apply(versions.plugin("android-library"))
      }

      extensions.configure<LibraryExtension> {
        applyCommonConfiguration()
      }
    }
  }
}

class AndroidComposeLibraryConventionPlugin : AndroidLibraryConventionPlugin() {
  override fun apply(target: Project) {
    super.apply(target)

    with(target) {
      with(pluginManager) {
        apply(versions.plugin("kotlin-compose"))
      }

      extensions.configure<LibraryExtension> {
        enableCompose()
      }
    }
  }
}

open class AndroidApplicationConventionPlugin : AndroidBaseConventionPlugin() {
  override fun apply(target: Project) {
    super.apply(target)

    with(target) {
      with(pluginManager) {
        apply(versions.plugin("android-application"))
      }

      extensions.configure<ApplicationExtension> {
        applyCommonConfiguration()
        applyApplicationConfiguration()
      }
    }
  }
}

class AndroidComposeApplicationConventionPlugin : AndroidApplicationConventionPlugin() {
  override fun apply(target: Project) {
    super.apply(target)

    with(target) {
      with(pluginManager) {
        apply(versions.plugin("kotlin-compose"))
      }

      extensions.configure<ApplicationExtension> {
        enableCompose()
      }
    }
  }
}
