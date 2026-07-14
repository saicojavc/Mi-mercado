# Mi mercado - Walkthrough

**Mi mercado** is a collaborative grocery shopping application built with modern Android technologies. It allows multiple users in a household to sync their shopping list in real-time.

## Tech Stack

- **UI Framework:** Jetpack Compose with Material 3 (Expressive design).
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines and StateFlow.
- **Backend:** Firebase Realtime Database for real-time data synchronization.
- **Image Loading:** Coil for efficient image rendering.
- **Dependency Management:** Gradle Version Catalog (`libs.versions.toml`).

## Key Features

- **Product Catalog:** Browse a list of common grocery items.
- **Category Filtering:** Quickly find products by category (Frutas, Verduras, Lácteos, etc.).
- **Real-time Cart:** Add items to a shared cart that updates instantly for everyone in the household.
- **Collaborative Sync:** Uses Realtime Database listeners to handle concurrent updates to item quantities.
- **Edge-to-Edge Experience:** Fully immersive UI that respects system bars and display cutouts.

## Firebase Realtime Database Configuration

To enable the real-time sync, you must set up Realtime Database rules in your Firebase Console.

### Household Structure
The app uses the following node structure:
`households/{householdId}/cart/{productId}`

The current default household ID is set to `familia_valdes` in [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt).

### Recommended Security Rules
Add the following rules to your Realtime Database to allow read/write access to the shopping cart:

```json
{
  "rules": {
    "households": {
      "$householdId": {
        "cart": {
          ".read": true,
          ".write": true
        }
      }
    }
  }
}
```

## Recent Changes

- **Firestore Removal**: Removed the unused `firebase-firestore` dependency from `app/build.gradle.kts` and `libs.versions.toml`.
- **Backend Sync**: The app now exclusively uses **Firebase Realtime Database** for collaborative shopping list synchronization, as reflected in `CartViewModel.kt`.
- **Project Stability**: Verified that the project compiles and runs successfully after dependency cleanup.

## Files Created/Updated

- [MainActivity.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MainActivity.kt): App entry point and theme setup.
- [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt): Logic for Realtime Database listeners and cart state.
- [build.gradle.kts](file:///D:/Jorgito/Proyects/Mimercado/app/build.gradle.kts) & [libs.versions.toml](file:///D:/Jorgito/Proyects/Mimercado/gradle/libs.versions.toml): Cleanup of unused Firestore dependency.
