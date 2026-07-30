# Plan de Eliminación de productId a favor de itemId

Este plan detalla la eliminación definitiva del campo `productId` en el modelo y la lógica de la aplicación, consolidando a `itemId` como el único identificador para los elementos del carrito en Firestore.

## User Review Required

> [!IMPORTANT]
> Al eliminar el campo `productId`, la identificación de "productos iguales" para incrementar cantidad ahora se basará en el prefijo del `itemId` (que contiene el ID del producto). Esto simplifica el modelo pero requiere filtrar los resultados en el cliente o mediante consultas de rango en Firestore.

## Proposed Changes

### Modelo de Datos

#### [MODIFY] [CartItem.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/CartItem.kt)
- Eliminar el campo `productId`.
- `itemId` será el único campo de identificación.

### Lógica de Negocio

#### [MODIFY] [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt)
- **`addToCart`**:
    - Obtener todos los ítems del usuario actual (`whereEqualTo("addedBy", userId)`).
    - Buscar localmente si alguno tiene un `itemId` que comience con `product.id + "_"`.
    - Si existe, incrementar cantidad del `itemId` encontrado.
    - Si no, generar nuevo `itemId = "${product.id}_${System.currentTimeMillis()}"` y guardar.
- **Operaciones**: Asegurar que `incrementQuantity`, `decrementQuantity` y `removeFromCart` operan exclusivamente sobre `itemId`.

### Cloud Functions

#### [MODIFY] [index.js](file:///D:/Jorgito/Proyects/Mimercado/functions/index.js)
- Eliminar cualquier referencia a `productId` en la desestructuración y logs.
- Utilizar `itemId` como identificador principal.

## Verification Plan

### Automated Tests
- Ejecutar `gradle assembleDebug` para confirmar que no hay errores de compilación tras la eliminación del campo.

### Manual Verification
1. Agregar "Leche" al carrito.
2. Verificar en Firestore que el documento NO tiene campo `productId`, solo `itemId`.
3. Agregar "Leche" de nuevo y verificar que se incrementa la cantidad en el mismo documento (detectado por prefijo).
4. Verificar que la Cloud Function se dispara correctamente usando el `itemId` del path.
