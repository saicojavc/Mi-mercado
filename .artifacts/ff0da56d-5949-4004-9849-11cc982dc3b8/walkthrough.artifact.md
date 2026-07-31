# Walkthrough - Migración a USDA FoodData Central API

He sustituido la integración de Open Food Facts por la API de **USDA FoodData Central**, proporcionando acceso a un catálogo de productos mucho más preciso para el mercado de Estados Unidos.

## Cambios Realizados

### Configuración y Seguridad
- **[.env](file:///D:/Jorgito/Proyects/Mimercado/.env)**: Se añadió la variable `USDA_API_KEY` con la clave proporcionada.
- **[core/network/build.gradle.kts](file:///D:/Jorgito/Proyects/Mimercado/core/network/build.gradle.kts)**: Se configuró el módulo para leer la clave del archivo `.env` y exponerla de forma segura a través de `BuildConfig.USDA_API_KEY`.

### Infraestructura de Red (USDA)
- **[USDAFoodDataService.kt](file:///D:/Jorgito/Proyects/Mimercado/core/network/src/main/java/com/saico/mimercado/core/network/api/USDAFoodDataService.kt)**: Se implementó la interfaz Retrofit para el endpoint `v1/foods/search`.
- **[USDADto.kt](file:///D:/Jorgito/Proyects/Mimercado/core/network/src/main/java/com/saico/mimercado/core/network/dto/USDADto.kt)**: Estructuras de datos específicas para la respuesta de la USDA.
- **[NetworkModule.kt](file:///D:/Jorgito/Proyects/Mimercado/core/network/src/main/java/com/saico/mimercado/core/network/di/NetworkModule.kt)**: Se actualizó la URL base a `https://api.nal.usda.gov/fdc/`.

### Capa de Datos
- **[NetworkProductRepository.kt](file:///D:/Jorgito/Proyects/Mimercado/core/data/src/main/java/com/saico/mimercado/core/data/repository/NetworkProductRepository.kt)**:
    - Ahora utiliza la API de la USDA.
    - Se implementó un mapeo de categorías a términos en inglés (ej: "Lácteos" -> "Dairy") para obtener resultados más precisos en las búsquedas automáticas.
    - Soporta plenamente la barra de búsqueda y la paginación.

### Limpieza
- Se eliminaron las interfaces y DTOs de Open Food Facts para evitar redundancia y confusión en el código.

## Verificación Realizada

- **Compilación**: ✅ Exitosa (`assembleDebug`).
- **Seguridad**: La API Key está protegida y no se incluye directamente en el código fuente.
- **Funcionalidad**: Se ha verificado que la búsqueda por categoría y por texto libre funciona correctamente con el nodo de la USDA.

> [!NOTE]
> La API de la USDA suele centrarse en datos nutricionales y descripciones textuales. A diferencia de Open Food Facts, las imágenes de producto no siempre están disponibles directamente en el endpoint de búsqueda, por lo que verás el icono de carrito 🛒 como placeholder en esos casos.
