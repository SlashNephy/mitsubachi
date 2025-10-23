import dev.detekt.gradle.Detekt
import dev.detekt.gradle.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.maybeCreate
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
        basePath.set(rootProject.projectDir.absolutePath)
        parallel.set(true)
        autoCorrect.set(true)

        reports {
          checkstyle.required.set(false)
          sarif.required.set(true)
          markdown.required.set(true)
        }

        exclude {
          it.file.path.contains("generated/")
        }
      }

      rootProject.tasks.maybeCreate<ReportMergeTask>("detektMergeReports").apply {
        input.from(
          tasks.withType<Detekt>().map { it.reports.sarif.outputLocation }
        )
        output.set(rootProject.layout.buildDirectory.file("reports/detekt/merged.sarif"))
      }
    }
  }
}
