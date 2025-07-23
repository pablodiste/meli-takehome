# MercadoLibre Android Take Home Exercise

## Overview

This project demonstrates a modular, scalable approach to Android app development.
It separates concerns across data, domain, and presentation layers, ensuring testability, maintainability, and clear boundaries.

### Video

https://github.com/user-attachments/assets/d9f20e97-c783-4e31-9e27-cab128454203

## Architecture

- **Clean Architecture**: Divides code into `data`, `domain`, and `ui` layers.
- **Repository Pattern**: Centralizes data access and business logic.
- **Dependency Injection**: Uses Hilt for lifecycle-aware DI.
- **Reactive Streams**: Kotlin Coroutines and Flow for async and event-driven data.
- **Jetpack Compose**: Declarative UI with state-driven design.
- **Navigation**: Compose Navigation for screen transitions.

### Layer Breakdown

- UI: **feature/**:

  - Compose screens
  - ViewModels
  - Navigation graph
- Domain: **core/domain/**:

  - Business models
  - Use cases
  - Repository interfaces
- Data: **core/data/**:

  - Local (Room) and remote (Network) data sources
  - Entity and DTO mapping
  - Repository implementations
- Common UI **core/ui/**:

  - UI components

## Dependencies

- **Kotlin**
- **Room** (androidx.room:room-runtime, room-ktx)
- **Hilt** (dagger.hilt.android)
- **Jetpack Compose** (material3, ui, navigation)
- **Coroutines** (kotlinx-coroutines-core, kotlinx-coroutines-android)
- **Lifecycle** (viewmodel, livedata, runtime)
- **JUnit** & **MockK** (unit testing)
- **Paging3** (data pagination)
- **Compose UI Test** (androidx.compose.ui:ui-test-junit4)

## Getting Started

1. Clone the repository or unzip the provided file
2. Sync Gradle and build the project
3. Run the app on an emulator or device

## Testing

* Unit tests:
  Run with ./gradlew test or use the configured IDE test runner.
* UI tests:
  Run with ./gradlew connectedAndroidTest

## Considerations

- Targeting API 35 mobile and tablet, portrait and landscape.
- Used Google architecture recommendations which are also based on Clean and community proposals. Module naming and location might vary.
- Added a room database and used it as a single source of truth, this allow better support to offline mode if we are accessing the data from remote sources.
- Supported a database cached paging implementation using the Paging3 library.
- Supported light and dark mode.
- Error handling is implemented using a sealed class to represent different states (loading, success, error).
- Logging was applied for critical errors using Timber.
- Errors are shown in the UI allowing to retry the operation.

## Next Steps

- In case the number of features in the features module grow, it is useful to separate them in different modules.
- The same could also happen with the data layer, we can implement different data sources in different modules.
- I have used Hilt but Koin is a great option too for CI.
- I have used MockK in tests, fake objects or any other library can be used instead if preferred.
- Included a simple compose android ui test. We could integrate robolectric as well to speed up the ui tests.
- Add more unit tests to cover more scenarios. For example data source or integration tests.
- Add more coverage to the different pagination events.
- Setup proguard to remove unused code and shrink the app size.
- Prepare CI/CD pipelines to automate builds and tests.
