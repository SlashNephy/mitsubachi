pluginManagement {
  includeBuild("build-logic")
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()

    // https://androidx.dev/
    // maven(uri("https://androidx.dev/snapshots/builds/[buildId]/artifacts/repository")) {
    //   content {
    //     includeGroupByRegex("androidx.*")
    //   }
    // }
  }
}

rootProject.name = "Mitsubachi"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(
  ":app",
  ":core:common",
  ":core:data",
  ":core:domain",
  ":core:testing",
  ":core:ui",
  ":core:ui-testing",
  ":feature:checkin",
  ":feature:home",
  ":feature:map",
  ":feature:settings",
  ":feature:welcome",
)
