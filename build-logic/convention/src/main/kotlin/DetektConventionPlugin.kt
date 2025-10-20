import dev.detekt.gradle.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("detekt"))
      }

      dependencies {
        add("detektPlugins", versions.library("detekt-rules-ktlint-wrapper"))
        add("detektPlugins", versions.library("detekt-rules-compose"))
      }

      tasks.withType<Detekt>().configureEach {
        buildUponDefaultConfig.set(true)
        allRules.set(true)
        config.setFrom(rootProject.file("detekt.yml"))
      }
    }
  }
}
