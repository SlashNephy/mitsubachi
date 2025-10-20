import com.google.protobuf.gradle.ProtobufExtension
import com.google.protobuf.gradle.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DataStoreConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("protobuf"))
      }

      extensions.configure<ProtobufExtension> {
        protoc {
          artifact = versions.artifact("protobuf-protoc")
        }

        generateProtoTasks {
          all().forEach { task ->
            task.builtins {
              id("java") {
                option("lite")
              }

              id("kotlin") {
                option("lite")
              }
            }
          }
        }
      }

      dependencies {
        "implementation"(versions.library("protobuf-kotlin-lite"))
        "implementation"(versions.library("androidx-datastore"))
      }
    }
  }
}
