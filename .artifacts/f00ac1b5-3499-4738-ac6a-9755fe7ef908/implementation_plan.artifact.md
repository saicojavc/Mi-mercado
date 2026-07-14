# Implementation Plan: Migrate Cart from Firestore to Realtime Database

Migrate the cart management system from Google Cloud Firestore to Firebase Realtime Database (RTDB) for improved real-time performance and reduced latency in cart operations.

## User Review Required

> [!IMPORTANT]
> This change replaces Firestore with Realtime Database for the cart feature. Ensure that the Firebase project has Realtime Database enabled and the rules are configured to allow read/write access at `households/familia_valdes/cart`.

## Proposed Changes

### Dependencies Layer

#### [MODIFY] [libs.versions.toml](file:///D:/Jorgito/Proyects/Mimercado/gradle/libs.versions.toml)
- [DELETE] `firebase-firestore` library definition.
- [NEW] `firebase-database = { group = "com.google.firebase", name = "firebase-database" }` library definition.

#### [MODIFY] [build.gradle.kts](file:///D:/Jorgito/Proyects/Mimercado/app/build.gradle.kts)
- [DELETE] `implementation("com.google.firebase:firebase-firestore")`.
- [NEW] `implementation(libs.firebase.database)`.

---

### UI/Logic Layer

#### [MODIFY] [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt)
- Replace `FirebaseFirestore` instance with `FirebaseDatabase` instance.
- Update `cartCollection` to a `DatabaseReference` pointing to `households/familia_valdes/cart`.
- Replace `addSnapshotListener` with a `ValueEventListener` in the `init` block.
- Rewrite `addToCart` to use RTDB's `runTransaction` for atomic increments.
- Rewrite `removeFromCart` to use `removeValue()`.
- Rewrite `clearCart` to use `removeValue()` on the entire cart reference.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure compilation.

### Manual Verification
1. **Real-time updates**: Add a product from `ProductListScreen` and verify the `ShoppingCart` FAB badge updates instantly.
2. **Atomic Increments**: Add the same product multiple times and verify the quantity increases correctly in the `CartScreen` bottom sheet.
3. **Removal**: Remove an item from the cart and verify it disappears from the list and the badge updates.
4. **Clear Cart**: Use the "Vaciar carrito" button and verify the cart becomes empty.
5. **Error Handling**: Monitor `Toast` messages for any RTDB connection or permission errors.
