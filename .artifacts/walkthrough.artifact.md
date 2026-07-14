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

## Screens and Components

| Component | Description |
| :--- | :--- |
| **Product List Screen** | The main dashboard where users can browse products and see their cart status. |
| **Cart Sheet** | A modal bottom sheet providing a quick overview of the shopping list and management tools. |
| **Category Filter** | A scrollable chip-based navigation bar for product discovery. |
| **Product Row** | An expressive card design for individual products with quick-add actions. |

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

## Files Created/Updated

- [MainActivity.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MainActivity.kt): App entry point and theme setup.
- [Product.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/Product.kt): Domain model and static product data.
- [CartItem.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/CartItem.kt): Model for items stored in the cloud cart.
- [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt): Logic for Realtime Database listeners and cart state.
- [ProductListScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/ProductListScreen.kt): Main UI layout with Scaffold and FAB.
- [CartScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/CartScreen.kt): UI for the shopping cart summary.
- [CategoryFilter.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/components/CategoryFilter.kt): Reusable category selection component.
- [ProductRow.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/components/ProductRow.kt): Card component for product display.
- [Theme.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/theme/Theme.kt) & [Color.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/theme/Color.kt): Material 3 color system and theme implementation.
- [strings.xml](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/res/values/strings.xml): Localized application strings.
- [build.gradle.kts](file:///D:/Jorgito/Proyects/Mimercado/app/build.gradle.kts) & [libs.versions.toml](file:///D:/Jorgito/Proyects/Mimercado/gradle/libs.versions.toml): Cleanup of unused Firestore dependency.

## Visual Interface

### Main Dashboard
The main screen features a clean, Material 3 top bar with the "Mi mercado" branding. Products are displayed in an expressive list with high-quality images. A floating action button with a badge shows the current number of items in the cart.

### Category Navigation
Users can swipe through a horizontal list of categories like "Frutas", "Lácteos", and "Carnes" to filter the catalog instantly.

### Real-time Sync
When an item is added, a badge appears on the FAB. If multiple users add the same item, the quantity increments automatically across all devices thanks to Realtime Database's listeners.
