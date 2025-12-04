
# FieldAgent

FieldAgent is a modern Android application that showcases a directory of agents. It is built with a focus on a clean, scalable, and maintainable architecture.

## About The Project

This project serves as a practical example of modern Android development practices. It demonstrates how to build a feature-rich application that is both user-friendly and robust. The app allows users to browse through a list of agents, view their profiles, and search for specific agents.

## Features

*   **Agent Directory:** View a paginated list of agents, with support for offline viewing.
*   **Search:** Search for agents by name or other attributes.
*   **Agent Profile:** View detailed information about each agent.
*   **Offline Support:** The app is designed to be offline-first, allowing users to browse previously loaded data without an internet connection.

## Tech Stack

*   **Kotlin:** The primary programming language for building the application.
*   **Coroutines:** For managing background threads and asynchronous operations.
*   **Flow:** A reactive stream library for observing data changes.
*   **Android Jetpack:**
    *   **Paging 3:** For loading and displaying large datasets in a paginated manner.
    *   **Room:** For local data persistence.
    *   **ViewModel:** To store and manage UI-related data.
    *   **Lifecycle:** To manage activity and fragment lifecycles.
*   **Retrofit:** A type-safe HTTP client for making network requests.
*   **Glide:** An image loading and caching library.
*   **Hilt:** For dependency injection.

## Architecture

The application follows the **Clean Architecture** pattern, which separates the codebase into distinct layers:

*   **UI Layer:** Displays the application data and sends user events to the domain layer. It uses the **MVVM (Model-View-ViewModel)** design pattern.
*   **Domain Layer:** Contains the business logic of the application. It consists of use cases that are executed by the UI layer.
*   **Data Layer:** Responsible for providing data to the domain layer. It manages data from multiple sources, such as a remote API and a local database.

## How To Build

1.  Clone the repository:
    ```sh
    git clone https://github.com/your-username/field-agent.git
    ```
2.  Open the project in Android Studio.
3.  Build the project using Gradle.
    ```sh
    ./gradlew build
    ```

