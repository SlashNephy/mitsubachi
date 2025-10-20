import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinSerializationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("kotlin-serialization"))
      }

      dependencies {
        "implementation"(versions.library("kotlinx-serialization-json"))
      }
    }
  }
}
