# Project Plan

Create a simple Android app called "Mi mercado" in Kotlin with Jetpack Compose to manage a real-time shared family shopping list using Firebase Firestore.
Key Features:
- Main screen with a hardcoded product catalog.
- Each product has an image (Coil), name, and a "+" button.
- Category filter with horizontal FilterChips (All, Dairy, Bakery, Meats, Fruits/Veg, Pantry, Cleaning, Beverages).
- Adding to cart saves/increments in Firestore: `households/familia_valdes/cart/{itemId}`.
- Firestore document fields: productId, name, imageUrl, category, quantity, timestamp.
- Real-time cart view (BottomSheet or Tab) using `addSnapshotListener`.
- Cart management: delete items (swipe/trash) and clear all.
- UI/UX: Material3, light/dark theme, rounded cards, cart badge.
- Architecture: Simple ViewModel with StateFlow, single Activity, no complex layers.
- Tech Stack: Kotlin, Jetpack Compose, Firebase Firestore BOM, Coil, Material3.

## Project Brief

# Project Brief: Mi mercado

## Features
1. **Product Catalog & Categorization**: Browse a visual catalog of products with high-quality images and filter them instantly using Material3 FilterChips (e.g., Dairy, Bakery, Meats, Fruits/Veg).
2. **Real-time Shared Shopping**: A collaborative cart that synchronizes across all family devices in real-time using Firebase Firestore, allowing members to see updates as they happen.
3. **Interactive Cart Management**: A dedicated cart view (via BottomSheet or Tab) that allows users to increment quantities, remove items with a swipe, or clear the entire list.
4. **Adaptive Material3 UI**: A responsive interface built with Material3 components, featuring rounded cards and a dynamic cart badge, designed to adapt seamlessly across different screen sizes.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Navigation**: Jetpack Navigation 3 (State-driven)
- **Adaptive Layout**: Compose Material Adaptive Library
- **Backend/Real-time**: Firebase Firestore (BOM)
- **Concurrency & State**: Kotlin Coroutines & StateFlow
- **Image Loading**: Coil
- **Architecture**: Simple ViewModel-based architecture (Single Activity)

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
Implemented comprehensive Firestore error handling and user feedback (Toasts).
Verified that the app is stable and UI components are functional.
Note: Users must configure Firestore rules (e.g., allow read, write: if true) in their Firebase Console to enable real-time features.
- **Acceptance Criteria:**
  - App does not crash
  - Build pass
  - Material3 Light/Dark themes functional
  - Real-time sync verified between Firestore and UI
- **Duration:** 14m 28s

