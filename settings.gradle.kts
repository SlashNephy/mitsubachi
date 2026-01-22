@file:Suppress("UnstableApiUsage")

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

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

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
  ":core:ui:common",
  ":core:ui:compose",
  ":core:ui:glance",
  ":core:ui:symbols",
  ":core:ui:wear",
  ":feature:checkin",
  ":feature:home",
  ":feature:map",
  ":feature:settings",
  ":feature:welcome",
  ":feature:widget:photo"
)
