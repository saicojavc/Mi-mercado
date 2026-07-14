# Project Plan

Configure a push notification system using Firebase Cloud Messaging (FCM) on top of the existing Firebase Realtime Database (RTDB) setup.
Note: The user's prompt mentions Firestore, but we migrated the app to Realtime Database (RTDB) in the previous steps.
We should integrate FCM using RTDB paths instead of Firestore.
Path structure in RTDB:
- `households/familia_valdes/users/{userId}`: { deviceToken: String, lastSeen: Long, username: String }
- `households/familia_valdes/cart/{productId}`: CartItem (which will now have `addedBy: String`)

Key Features:
1. Register FCM token for each device in RTDB under `households/familia_valdes/users/{userId}`.
2. Update `CartItem` model to include `addedBy: String`.
3. Create `MyFirebaseMessagingService.kt` to handle new tokens and show background/foreground notifications.
4. Manage unique `userId` in `SharedPreferencesUtil.kt`.
5. Request POST_NOTIFICATIONS permission dynamically on Android 13+.
6. In ViewModel, send the `addedBy` field with `userId` during add-to-cart operations.
7. Include the Node.js Cloud Function equivalent for Realtime Database:
   `functions.database.ref('/households/{householdId}/cart/{itemId}').onCreate(...)`

## Project Brief

# Project Brief: Shared Cart & Push Notifications MVP

## Features

1. **User Identity & FCM Token Registration**
   Generates and manages a unique, persistent `userId` stored in `SharedPreferences` to represent each active member of the household. Upon startup, the app registers/updates the device's FCM registration token in the Firebase Realtime Database under the path `households/familia_valdes/users/{userId}`.

2. **Collaborative Cart Attribution**
   Updates the shared `CartItem` model to include an `addedBy` field (storing the adding user's ID). During add-to-cart operations within the ViewModel, the app populates this property to identify who modified the household cart.

3. **In-App and Background Push Notifications**
   Features a custom `MyFirebaseMessagingService` that processes incoming cloud messages. It seamlessly handles payload structures to deliver and show Android notifications whether the app is in the foreground or working in the background.

4. **Dynamic Permission Handling (Android 13+)**
   Ensures compliance with modern Android security standards by dynamically requesting the `POST_NOTIFICATIONS` permission on Android 13+ (API level 33) before activating notification features.

---

## High-Level Technical Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Declarative UI with Material 3 styling)
- **Navigation & Adaptive Layouts:** Strictly utilizing **Jetpack Navigation 3** (state-driven navigation) paired with **Compose Material Adaptive** components for responsive multi-form factor rendering.
- **Concurrency:** Kotlin Coroutines & Flow for asynchronous operations.
- **Backend & Real-time Sync:**
  - **Firebase Realtime Database (RTDB):** Tracks shared cart status and active device tokens across the household.
  - **Firebase Cloud Messaging (FCM):** Delivers cross-device notifications.
- **Local Storage:** SharedPreferences for simple user profile/identity management.

### Household DB Structure Reference
```json
{
  "households": {
    "familia_valdes": {
      "users": {
        "userId_123": {
          "deviceToken": "fcm_token_string",
          "lastSeen": 1690000000000,
          "username": "Juan"
        }
      },
      "cart": {
        "productId_abc": {
          "name": "Manzanas",
          "quantity": 3,
          "addedBy": "userId_123"
        }
      }
    }
  }
}
```

### Server-Side Cloud Trigger (Equivalent Node.js Concept)
To dispatch push notifications, a database trigger executes whenever a new cart item is added:
```javascript
exports.onCartItemCreated = functions.database
  .ref('/households/{householdId}/cart/{itemId}')
  .onCreate(async (snapshot, context) => {
    const item = snapshot.val();
    const addedByUserId = item.addedBy;
    const householdId = context.params.householdId;
    
    // 1. Fetch household users from `/households/{householdId}/users`
    // 2. Locate tokens of all users EXCEPT the addedByUserId
    // 3. Send payload through FCM Admin SDK: "Someone added {item.name} to the cart!"
  });
```

---

## UI Design Image

![UI Design](D:/Jorgito/Proyects/Mimercado/input_images/cart_push_notification_brief.jpg)
Image path = D:/Jorgito/Proyects/Mimercado/input_images/cart_push_notification_brief.jpg

## Implementation Steps
**Total Duration:** 24m 24s

### Task_1_Setup_and_Models: Configure project-level and app-level build.gradle files with dependencies for Firebase Firestore (BOM), Coil, Jetpack Navigation 3, and Material 3 Adaptive. Initialize Firebase using the provided configuration and define Product and CartItem data models.
- **Status:** COMPLETED
- **Updates:** Dependencies for Firebase Firestore, Coil, and Material3 verified.
- **Acceptance Criteria:**
  - Project builds successfully
  - Firebase initialized
  - Data models defined for Product and CartItem
- **Duration:** 1m 5s

### Task_2_Catalog_UI: Implement the main screen using Jetpack Compose and Material 3. This includes a top search/header area, horizontal FilterChips for product categories, and a LazyVerticalGrid of product cards. Each card must show a product image (Coil), name, and a '+' button.
- **Status:** COMPLETED
- **Updates:** Implemented ProductListScreen with CategoryFilter and ProductRow.
- **Acceptance Criteria:**
  - Main screen UI displays product catalog
  - Filtering by category works
  - Product images load correctly using Coil
- **Duration:** 1m 27s

### Task_3_Firestore_and_Cart_Logic: Develop a Firestore repository to manage the cart at 'households/familia_valdes/cart'. Implement the logic to add or increment items in Firestore. Create a real-time Cart BottomSheet that listens to updates via addSnapshotListener and displays a dynamic cart badge on the main screen.
- **Status:** COMPLETED
- **Updates:** Implemented CartViewModel with Firestore real-time listener (addSnapshotListener).
- **Acceptance Criteria:**
  - Adding/Incrementing items updates Firestore
  - Cart BottomSheet shows real-time data
  - Cart badge reflects current total items
- **Duration:** 1m 21s

### Task_4_Final_Polish_and_Verify: Refine the UI with Material3 Light/Dark theme support and adaptive layout adjustments. Perform a final run and verify application stability (no crashes). Confirm alignment with all user requirements.
- **Status:** COMPLETED
- **Updates:** Refined UI with Material3 Light/Dark themes and dynamic colors.
- **Acceptance Criteria:**
  - App does not crash
  - Build pass
  - Material3 Light/Dark themes functional
  - Real-time sync verified between Firestore and UI
- **Duration:** 14m 28s

### Task_5_FCM_Dependencies_And_Service: Configure build.gradle with Firebase Cloud Messaging (FCM) and Realtime Database dependencies. Implement SharedPreferencesUtil to manage a persistent unique 'userId'. Update the CartItem model to include 'addedBy'. Implement MyFirebaseMessagingService to receive push notification payloads and display Android notifications.
- **Status:** COMPLETED
- **Updates:** Configured FCM dependencies in gradle files.
- **Acceptance Criteria:**
  - Firebase messaging and database dependencies added
  - SharedPreferencesUtil correctly stores and retrieves persistent userId
  - CartItem data class is updated with 'addedBy' field
  - MyFirebaseMessagingService handles incoming push messages for both foreground and background states
  - The implemented UI must match the design provided in D:/Jorgito/Proyects/Mimercado/input_images/cart_push_notification_brief.jpg
- **Duration:** 3m 2s

### Task_6_User_Registration_And_Permission: Upon app startup, register/update the user identity and device FCM token in RTDB at households/familia_valdes/users/{userId}. Request the POST_NOTIFICATIONS permission dynamically on Android 13+. In the Cart ViewModel, set the addedBy property to userId when items are added to the cart. Provide the server-side Cloud Function JavaScript trigger concept as documentation. Run and verify application stability with critic_agent.
- **Status:** COMPLETED
- **Updates:** Implemented dynamic permission request for POST_NOTIFICATIONS in MainActivity.
Implemented user identity registration (userId, deviceToken, lastSeen, username) in RTDB under 'households/familia_valdes/users/{userId}' on app startup.
Updated CartViewModel to set the 'addedBy' field with the local userId during add-to-cart operations.
Wrote the Node.js Cloud Function equivalent in 'rtdb_cloud_function_concept.js'.
Verified stability with critic_agent. All tests and builds pass.
- **Acceptance Criteria:**
  - User ID and FCM token registered in RTDB path on startup
  - POST_NOTIFICATIONS permission requested on Android 13+ devices
  - Cart items added to database are correctly tagged with addedBy field
  - Node.js Cloud Function equivalent snippet provided/written
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - The implemented UI must match the design provided in D:/Jorgito/Proyects/Mimercado/input_images/cart_push_notification_brief.jpg
- **Duration:** 3m 1s

