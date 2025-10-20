import androidx.room.gradle.RoomExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("ksp"))
        apply(versions.plugin("room"))
      }

      extensions.configure<RoomExtension> {
        schemaDirectory(project.file("schemas").toString())
      }

      dependencies {
        "ksp"(versions.library("androidx-room-compiler"))
        "implementation"(versions.library("androidx-room-runtime"))
        "implementation"(versions.library("androidx-room-ktx"))
        "testImplementation"(versions.library("androidx-room-testing"))
      }
    }
  }
}
