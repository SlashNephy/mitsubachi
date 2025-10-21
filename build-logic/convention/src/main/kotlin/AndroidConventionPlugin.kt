import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

sealed interface AndroidProjectType {
  val enableCompose: Boolean

  data class Library(override val enableCompose: Boolean) : AndroidProjectType
  data class Application(override val enableCompose: Boolean) : AndroidProjectType
}

open class AndroidBaseConventionPlugin(private val projectType: AndroidProjectType) :
  Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("kotlin-android"))

        when (projectType) {
          is AndroidProjectType.Library -> {
            apply(versions.plugin("android-library"))
          }

          is AndroidProjectType.Application -> {
            apply(versions.plugin("android-application"))
          }
        }

        if (projectType.enableCompose) {
          apply(versions.plugin("kotlin-compose"))
        }
      }

      tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_21)
        }
      }

      listOfNotNull(
        extensions.findByType<LibraryExtension>(),
        extensions.findByType<ApplicationExtension>(),
      ).forEach { extension ->
        extension.apply {
          compileSdk = 36

          defaultConfig {
            minSdk = 36
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          }
          if (this is ApplicationExtension) {
            defaultConfig {
              targetSdk = 36
            }
          }

          buildFeatures {
            buildConfig = true
            compose = projectType.enableCompose
          }

          compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
          }

          lint {
            checkDependencies = true
          }

          testOptions {
            unitTests {
              isIncludeAndroidResources = true
              isReturnDefaultValues = true
              all {
                it.useJUnitPlatform()
              }
            }
          }

          tasks.withType<Test>().configureEach {
            failOnNoDiscoveredTests.set(false)
          }
        }
      }
    }
  }
}

class AndroidLibraryConventionPlugin : AndroidBaseConventionPlugin(
  AndroidProjectType.Library(enableCompose = false),
)

class AndroidComposeLibraryConventionPlugin : AndroidBaseConventionPlugin(
  AndroidProjectType.Library(enableCompose = true),
)

class AndroidComposeApplicationConventionPlugin : AndroidBaseConventionPlugin(
  AndroidProjectType.Application(enableCompose = true),
)
