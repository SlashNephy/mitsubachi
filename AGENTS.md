# AGENTS.md

You are an experienced Android app developer.

## Project Overview

Mitsubachi is an Android application that integrates with the Foursquare API, following a
modularized Clean Architecture approach. The project is structured to ensure separation of concerns,
scalability, and maintainability.

## Architecture Pattern

The application follows **Clean Architecture** principles with clear separation between layers:

- **Presentation Layer**: UI components and ViewModels
- **Domain Layer**: Business logic and use cases
- **Data Layer**: Data sources and repository implementations

## Technology Stack

Always keep an eye on the latest technology.

Whenever possible, use the features and libraries of the newest version.
Don't hesitate to use early access versions.

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Networking**: Ktor Client with Ktorfit
- **Database**: Room (encrypted) and Proto Datastore (unencrypted)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Build System**: Gradle
- **CI/CD**: GitHub Actions
- **Distribution**: Firebase App Distribution

## Design Principles

1. **Separation of Concerns**: Each layer has a distinct responsibility.
2. **Dependency Inversion**: High-level modules don't depend on low-level modules.
3. **Single Responsibility**: Each module focuses on a specific feature or functionality.
4. **Security First**: Always strive to write secure code. Be mindful of implementation best
   practices.
5. **Testability**: Clear interfaces and dependency injection enable easy testing.
6. **Internationalization**: When presenting strings that require localization, add string resources
   for each language.

## Module Structure

If new modules are added, provide a summary of the modules into below list.

Ensure the architecture unit test (`app/src/test/java/blue/starry/mitsubachi/ArchitectureTest.kt`)
is not violated.

- **:app**: Main application entry point
- **:core**: Core modules
- **:feature**: Feature modules
- **:build-logic**: Gradle convention plugin for shared build configuration

### Core Modules (:core:*)

- **:core/domain**: Business logic and use cases
    - Defines interfaces for data layer
    - Independent of Android framework and implementation details
- **:core/data**: Data sources such as network or database implementations.
    - Provides concrete implementations for interfaces defined in :core/domain
    - Handles data retrieval, caching, and persistence
- **:core/ui**: Reusable UI components
- **:core/common**: Shared utilities and extensions

### Feature Modules (:feature:*)

- **:feature:welcome**: User onboarding and authentication
- **:feature:home**: Home timeline
- **:feature:checkin**: Check-in functionality
- **:feature:map**: Map view and location display
- **:feature:settings**: Application settings
- **:feature:widget**: Widget implementation

## Data Flow

```
UI (Composable) → ViewModel → UseCase → Repository → Network/Database
     ↑                                                      ↓
     └──────────────── Flow/State ←───────────────────────┘
```

## Commands

The following are typical commands used for local development.

- **Build**: `./gradlew assembleLocalDebug`
- **Unit Test**: `./gradlew testLocalDebug`
- **Instrumented Test**: `./gradlew connectedLocalDebugAndroidTest`
- **Lint (Android Lint)**: `./gradlew lintLocalDebug`
- **Lint (detekt)**: `./gradlew detekt`

If desired, you can restrict the target by prefixing the module name like
`./gradlew :core:ui:compose:assembleDebug` for speed-up.
