import de.jensklingenberg.ktorfit.gradle.ErrorCheckingMode
import de.jensklingenberg.ktorfit.gradle.KtorfitPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class KtorfitConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply(versions.plugin("ktorfit"))
      }

      extensions.configure<KtorfitPluginExtension> {
        errorCheckingMode.set(ErrorCheckingMode.ERROR)
      }

      dependencies {
        "implementation"(versions.library("ktorfit-lib-light"))
      }
    }
  }
}
