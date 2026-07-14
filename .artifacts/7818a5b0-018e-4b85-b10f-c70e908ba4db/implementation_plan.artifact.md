# Implementation Plan - Task_2_Catalog_UI

Implement the catalog UI with category filtering, product rows, and a main product list screen.

## User Review Required

> [!NOTE]
> I will add a hardcoded list of products to `Product.kt` as it was requested but not present in the file.
> I will use a simple `ViewModel` to handle the filtering logic and state.

## Proposed Changes

### Data Layer

#### [MODIFY] [Product.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/Product.kt)
- Add a companion object with a `sampleProducts` list.

### UI Components

#### [NEW] [CategoryFilter.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/components/CategoryFilter.kt)
- Create `CategoryFilter` composable with a horizontal `LazyRow` and `FilterChip`s.

#### [NEW] [ProductRow.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/components/ProductRow.kt)
- Create `ProductRow` composable with `Card`, `AsyncImage`, and an add button.

### Screens & ViewModels

#### [NEW] [ProductListViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/ProductListViewModel.kt)
- Create `ProductListViewModel` to manage the selected category and filtered product list.

#### [NEW] [ProductListScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/ProductListScreen.kt)
- Create `ProductListScreen` composable with `Scaffold`, `TopAppBar`, and the product list.

### Main Entry Point

#### [MODIFY] [MainActivity.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MainActivity.kt)
- Update `MainActivity` to display `ProductListScreen`.

## Verification Plan

### Automated Tests
- I will run `./gradlew :app:assembleDebug` to ensure it builds.

### Manual Verification
- I will include `@Preview` for all components and the main screen to verify the UI visually (though I can't "see" it, the code will be structured correctly for it).
