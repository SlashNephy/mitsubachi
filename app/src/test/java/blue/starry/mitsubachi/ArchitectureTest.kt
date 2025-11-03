package blue.starry.mitsubachi

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchitectureTest {
  private val importedClasses = ClassFileImporter()
    .withImportOption(ImportOption.DoNotIncludeTests())
    .importPackages(BuildConfig.NAMESPACE)

  @Test
  fun dataLayerShouldNotDependOnUiLayer() {
    val rule = noClasses()
      .that()
      .resideInAPackage("..data..")
      .should()
      .dependOnClassesThat()
      .resideInAPackage("..ui..")

    rule.check(importedClasses)
  }

  @Test
  fun domainLayerShouldNotDependOnDataLayer() {
    val rule = noClasses()
      .that()
      .resideInAPackage("..domain..")
      .should()
      .dependOnClassesThat()
      .resideInAPackage("..data..")

    rule.check(importedClasses)
  }

  @Test
  fun domainLayerShouldNotDependOnUiLayer() {
    val rule = noClasses()
      .that()
      .resideInAPackage("..domain..")
      .should()
      .dependOnClassesThat()
      .resideInAPackage("..ui..")

    rule.check(importedClasses)
  }

  @Test
  fun domainLayerShouldNotDependOnAndroidPackages() {
    val rule = noClasses()
      .that()
      .resideInAPackage("..domain..")
      .should()
      .dependOnClassesThat()
      .resideInAPackage("android..")

    rule.check(importedClasses)
  }

  @Test
  fun uiLayerShouldNotDependOnDataLayer() {
    val rule = noClasses()
      .that()
      .resideInAPackage("..ui..")
      .should()
      .dependOnClassesThat()
      .resideInAPackage("..data..")

    rule.check(importedClasses)
  }
}
