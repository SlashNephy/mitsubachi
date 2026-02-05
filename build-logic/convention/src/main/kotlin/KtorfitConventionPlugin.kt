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
        // Set compiler plugin version to match Kotlin 2.3.0 compatibility
        // See: https://github.com/Foso/Ktorfit/issues/1010
        compilerPluginVersion.set("2.3.3")
      }

      dependencies {
        "implementation"(versions.library("ktorfit-lib-light"))
      }
    }
  }
}
