# Walkthrough - Sustitución Total de productId por itemId

He completado la eliminación definitiva del campo `productId` en favor de `itemId` en toda la aplicación (Android y Cloud Functions), unificando el identificador para los elementos del carrito.

## Cambios Realizados

### Modelo de Datos
- **[CartItem.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/CartItem.kt)**: Se eliminó por completo el campo `productId`. Ahora el objeto solo utiliza `itemId`.

### Lógica del Carrito (Android)
- **[CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt)**:
    - **`addToCart`**: Se implementó una lógica de búsqueda por prefijo. Ahora, para detectar si un producto ya existe en el carrito del usuario, se comprueba si algún `itemId` (ID del documento) comienza con `product.id + "_"`.
    - **Operaciones Unificadas**: Todas las funciones (`incrementQuantity`, `decrementQuantity`, `removeFromCart`) ahora operan exclusivamente basándose en el `itemId`.

### Cloud Functions
- **[index.js](file:///D:/Jorgito/Proyects/Mimercado/functions/index.js)**:
    - Se actualizó el disparador `onCreate` para usar `itemId` en lugar de `productId` en los parámetros del contexto.
    - Se eliminaron las referencias a `productId` en los logs y la lógica interna para evitar errores de tipo `undefined`.

## Verificación Realizada

- **Compilación**: La aplicación Android compila sin errores tras la eliminación del campo `productId`.
- **Lógica de Documentos**: Se ha asegurado que el patrón de ID en Firestore (`{productId}_{timestamp}`) permita la identificación unívoca por prefijo.

> [!TIP]
> Al haber eliminado `productId`, los documentos antiguos que aún tengan ese campo no causarán errores, pero las nuevas entradas serán más limpias y consistentes con la arquitectura de `itemId` único.

> [!IMPORTANT]
> Recuerda desplegar la Cloud Function actualizada para que los cambios en el filtrado de notificaciones surtan efecto inmediatamente.
