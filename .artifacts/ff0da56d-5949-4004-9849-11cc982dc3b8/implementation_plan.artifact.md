# Implementación de Favoritos Familiares y Vista "Habitual"

Este plan detalla la migración de la pantalla principal a una vista de productos habituales sincronizada con Firestore y la adición del sistema de favoritos.

## Proposed Changes

### Core Model
#### [MODIFY] [Product.kt](file:///D:/Jorgito/Proyects/Mimercado/core/model/src/main/java/com/saico/mimercado/core/model/Product.kt)
- Añadir campo `isFavorite: Boolean = false`.

### Core Data / Firestore
#### [NEW] [FavoriteRepository.kt](file:///D:/Jorgito/Proyects/Mimercado/core/domain/src/main/java/com/saico/mimercado/core/domain/repository/FavoriteRepository.kt)
- Interfaz para `toggleFavorite`, `getFavoritesFlow` y `isFavorite`.

#### [NEW] [FirestoreFavoriteRepository.kt](file:///D:/Jorgito/Proyects/Mimercado/core/data/src/main/java/com/saico/mimercado/core/data/repository/FirestoreFavoriteRepository.kt)
- Implementación usando Firestore. Los productos se guardarán en una colección `family_favorites`.

### Feature Products

#### [MODIFY] [ProductListViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/feature/products/src/main/java/com/saico/mimercado/feature/products/ProductListViewModel.kt)
- Añadir estado `ListMode` (HABITUAL vs DISCOVER).
- Por defecto iniciar en `HABITUAL`.
- Integrar el flujo de favoritos de Firestore.

#### [MODIFY] [ProductListScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/feature/products/src/main/java/com/saico/mimercado/feature/products/ProductListScreen.kt)
- Añadir `TabRow` o `SecondaryTabRow` para cambiar entre secciones.
- Mantener filtros y buscador en ambas vistas.

#### [MODIFY] [ProductDetailsScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/feature/products/src/main/java/com/saico/mimercado/feature/products/ProductDetailsScreen.kt)
- Añadir icono de corazón en la TopBar o junto al título.
- Lógica para guardar/eliminar de Firestore.

## Verification Plan

### Automated Tests
- `gradle assembleDebug` para verificar integridad.

### Manual Verification
1. Abrir app -> Debe mostrar "Habitual" (vacío al inicio).
2. Cambiar a "Descubrir" -> Buscar "Oreo" -> Entrar a detalles.
3. Marcar favorito (Corazón) -> Volver atrás.
4. Cambiar a "Habitual" -> El producto debe aparecer allí.
5. Verificar en consola de Firebase que los datos están en `family_favorites`.
