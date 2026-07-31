# Implementación de Detalles de Producto

Este plan detalla la creación de una vista de detalles que se despliega al tocar un producto, integrando datos extendidos de la USDA.

## User Review Required

> [!TIP]
> Utilizaremos un `ModalBottomSheet` para mostrar los detalles de forma rápida sin salir de la lista, o una pantalla completa según la navegación. Dado que solicitaste una "pestaña que se despliegue", implementaremos un Bottom Sheet expansible.

## Proposed Changes

### Core Network

#### [MODIFY] [USDAFoodDataService.kt](file:///D:/Jorgito/Proyects/Mimercado/core/network/src/main/java/com/saico/mimercado/core/network/api/USDAFoodDataService.kt)
- Añadir endpoint `GET v1/food/{fdcId}` para obtener detalles completos.

### Core Domain / Data

#### [MODIFY] [ProductRepository.kt](file:///D:/Jorgito/Proyects/Mimercado/core/domain/src/main/java/com/saico/mimercado/core/domain/repository/ProductRepository.kt)
- Añadir método `getProductDetails(fdcId: String): Result<ProductDetails>`.

#### [MODIFY] [NetworkProductRepository.kt](file:///D:/Jorgito/Proyects/Mimercado/core/data/src/main/java/com/saico/mimercado/core/data/repository/NetworkProductRepository.kt)
- Implementar la llamada a los detalles.

### Core UI (Navegación)

#### [MODIFY] [NavigationCommand.kt](file:///D:/Jorgito/Proyects/Mimercado/core/ui/src/main/java/com/saico/mimercado/core/ui/navigation/NavigationCommand.kt)
- Añadir `ProductDetailsRoute(val fdcId: String)` a las rutas tipadas.

### Feature Products

#### [NEW] [ProductDetailsViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/feature/products/src/main/java/com/saico/mimercado/feature/products/ProductDetailsViewModel.kt)
- Gestionar la carga de datos del producto seleccionado.

#### [NEW] [ProductDetailsScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/feature/products/src/main/java/com/saico/mimercado/feature/products/ProductDetailsScreen.kt)
- UI para mostrar la imagen grande, ingredientes, tabla nutricional y marca.

#### [MODIFY] [ProductRow.kt](file:///D:/Jorgito/Proyects/Mimercado/core/ui/src/main/java/com/saico/mimercado/core/ui/components/ProductRow.kt)
- Hacer que la tarjeta sea clicable para disparar la navegación.

## Verification Plan

### Automated Tests
- Ejecutar `gradle assembleDebug`.

### Manual Verification
1. Tocar un producto (ej: "Angel Food Cake").
2. Verificar que se despliega la vista de detalles.
3. Confirmar que se muestran los ingredientes y la marca oficial.
