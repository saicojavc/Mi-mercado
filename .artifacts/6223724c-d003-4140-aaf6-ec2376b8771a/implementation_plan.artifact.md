# Implementation Plan - Fix Firestore Connectivity and Error Handling

Improve Firestore stability by adding comprehensive error handling in `CartViewModel` and providing user feedback in the UI when operations fail.

## User Review Required

> [!IMPORTANT]
> To test these changes, ensure your Firebase project is correctly configured and that your Firestore rules allow access. For testing, you can use:
> `allow read, write: if true;`
> Or more securely:
> `allow read, write: if request.auth != null;` (if authentication is implemented).

## Proposed Changes

### ViewModel Layer

#### [MODIFY] [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt)
- Add a `MutableSharedFlow<String>` to broadcast error messages to the UI.
- Update `init` block to handle errors in `addSnapshotListener`.
- Update `addToCart` to handle transaction failures and document update errors.
- Update `removeFromCart` and `clearCart` with failure listeners.
- Use explicit logging to help diagnose connectivity issues.

### UI Layer

#### [MODIFY] [CartScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/CartScreen.kt)
- Observe the `errorMessages` flow from `CartViewModel` and display them using a `Toast`.

#### [MODIFY] [ProductListScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/ProductListScreen.kt)
- Observe the `errorMessages` flow from `CartViewModel` and display them using a `Toast`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure the project still builds correctly.

### Manual Verification
- Verify that errors are caught and logged.
- Verify that the user receives a notification (Toast) if Firestore operations fail (e.g., if rules deny access).
