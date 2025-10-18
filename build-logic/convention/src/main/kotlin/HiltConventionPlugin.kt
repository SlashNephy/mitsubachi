import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("ksp"))
        apply(versions.plugin("hilt"))
      }

      dependencies {
        "ksp"(versions.library("hilt-android-compiler"))
        "implementation"(versions.library("hilt-android-runtime"))
        "implementation"(versions.library("androidx-hilt-navigation-compose"))
      }
    }
  }
}
