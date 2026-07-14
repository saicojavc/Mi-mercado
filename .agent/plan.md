# Project Plan

Migrate "Mi mercado" app from Firebase Firestore to Firebase Realtime Database (RTDB) for cart management.
Key changes:
- Update dependencies in `app/build.gradle.kts` to include RTDB.
- Rewrite `CartViewModel.kt` to use `FirebaseDatabase` and `ValueEventListener`.
- Maintain the same real-time functionality and UI.
- Use RTDB path: `households/familia_valdes/cart`.


## Project Brief

# Project Brief: Mi mercado (MVP)

## Features
1. **Real-time Cart Synchronization**: A collaborative shopping list that updates instantly across all household devices using Firebase Realtime Database.
2. **Item Management**: Effortlessly add, remove, and update grocery items within the cart.
3. **Household Scoping**: Dedicated data path (`households/familia_valdes/cart`) ensures items are synced only within the specific family group.
4. **Reactive UI Updates**: The interface automatically reflects database changes in real-time without manual refreshes.

## High-Level Technical Stack
- **Kotlin**: Main language for modern, concise Android development.
- **Jetpack Compose**: Declarative UI toolkit for building the app's interface.
- **Kotlin Coroutines**: Used for handling asynchronous database listeners and background tasks.
- **Jetpack Navigation 3**: A state-driven navigation framework for managing app screens and flows.
- **Compose Material Adaptive**: Ensures the UI scales gracefully across different device form factors.
- **Firebase Realtime Database (RTDB)**: The core backend service for low-latency, real-time data synchronization.

## Implementation Steps
**Total Duration:** 18m 21s

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

