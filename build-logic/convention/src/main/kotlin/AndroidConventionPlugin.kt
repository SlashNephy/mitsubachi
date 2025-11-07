import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.kotlin
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

          packaging {
            resources {
              merges += "META-INF/LICENSE.md"
              merges += "META-INF/LICENSE-notice.md"
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
            checkReleaseBuilds = true
            checkTestSources = true
            checkDependencies = true
            checkAllWarnings = true
            ignoreWarnings = false
            showAll = true
            explainIssues = true
            sarifReport = true
            lintConfig = rootProject.file("android-lint.xml")
            baseline = rootProject.file("android-lint-baseline.xml")
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

      dependencies {
        // Convention Plugin では全モジュールで共通の依存のみを定義する

        // Unit Testing
        "testImplementation"(kotlin("test"))
        "testImplementation"(versions.library("junit-jupiter"))
        "testRuntimeOnly"(versions.library("junit-jupiter-engine"))
        "testRuntimeOnly"(versions.library("junit-platform-launcher"))
        "implementation"(versions.library("mockk")) // testImplementation で十分だけど @Preview でモックしたいことがある
        "testImplementation"(versions.library("archunit"))

        // Instrumented Testing
        "androidTestImplementation"(kotlin("test"))
        "androidTestImplementation"(versions.library("androidx-test-core"))
        "androidTestImplementation"(versions.library("androidx-test-ext-junit"))
        "androidTestImplementation"(versions.library("androidx-test-espresso-core"))

        if (projectType.enableCompose) {
          // BOM
          "implementation"(platform(versions.library("androidx-compose-bom")))
          "androidTestImplementation"(platform(versions.library("androidx-compose-bom")))

          // @Preview
          "implementation"(versions.library("androidx-compose-ui-tooling-preview"))
          "debugImplementation"(versions.library("androidx-compose-ui-tooling"))

          // Test
          "androidTestImplementation"(versions.library("androidx-compose-ui-test-junit4"))
          "debugImplementation"(versions.library("androidx-compose-ui-test-manifest"))
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
