package blue.starry.mitsubachi

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Disabled
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
  @Disabled("domain レイヤーから android.content.Intent を参照している箇所がある") // TODO
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

  @Test
  fun uiLayerShouldNotDependOnInterfacesFromDomainLayer() {
    val rule = noClasses()
      .that()
      .resideInAPackage("..ui..")
      .should()
      .dependOnClassesThat(
        resideInAPackage("..domain.usecase..")
          .and(JavaClass.Predicates.INTERFACES),
      )

    rule.check(importedClasses)
  }
}
