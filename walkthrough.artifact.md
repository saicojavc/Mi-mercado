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
- **Unique User Identification**: Implemented localized user management using SharedPreferences to generate and store a persistent, unique `userId` (`UUID.randomUUID()`) for each device.
- **Real-time User Registry**: Registered each active user under `households/familia_valdes/users/{userId}` in the Realtime Database on application startup, storing their token, username, and last seen timestamp.
- **Cart Metadata Enhancement**: Updated the `CartItem` model to include an `addedBy` field. This property captures the unique ID of the user who performed the most recent action on the item (adding or editing the quantity), allowing subsequent backend logic to know who triggered the changes.
- **FCM Real-time Notifications**: Integrated Firebase Cloud Messaging (FCM) by creating `MyFirebaseMessagingService.kt` to handle incoming data and notification payloads in both background (system notifications) and foreground (immediate on-screen Toast messages).
- **Post Notifications Permission (Android 13+)**: Added a dynamic, runtime notification permission check in `MainActivity.kt` for API level 33 and higher to request `POST_NOTIFICATIONS` properly on modern Android versions.
- **FCM Server-Side Notification Trigger (Cloud Function Concept)**: Introduced a Node.js-based Firebase Cloud Function architecture concept in `rtdb_cloud_function_concept.js` which detects writes to `/households/familia_valdes/cart/{productId}` and dispatches multi-cast FCM push notifications to all household members' registered devices *except* the user (matched by `addedBy`) who initiated the update.
- **Project Stability**: Verified that the project compiles and runs successfully after dependency cleanup and feature integration.

## Files Created/Updated

- [MainActivity.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MainActivity.kt): Dynamic runtime notifications permission launcher and app-start user/device token registry.
- [MyFirebaseMessagingService.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MyFirebaseMessagingService.kt): Custom FCM service implementing token refresh updates and messaging routing (system tray / Toasts).
- [CartItem.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/CartItem.kt): Enhanced model with `addedBy` metadata support.
- [SharedPreferencesUtil.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/util/SharedPreferencesUtil.kt): Key-value utility class that safely persists a unique, random UUID on the device.
- [rtdb_cloud_function_concept.js](file:///D:/Jorgito/Proyects/Mimercado/rtdb_cloud_function_concept.js): Reference implementation for the Firebase RTDB-triggered notification dispatching script.
- [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt): Logic for Realtime Database listeners and cart state.
- [build.gradle.kts](file:///D:/Jorgito/Proyects/Mimercado/app/build.gradle.kts) & [libs.versions.toml](file:///D:/Jorgito/Proyects/Mimercado/gradle/libs.versions.toml): Cleanup of unused Firestore dependency.
