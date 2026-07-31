# Implementación de Imágenes mediante CDN de Retailers (Estrategia Target)

Este plan detalla la implementación de una solución de alto rendimiento y costo cero para mostrar imágenes de productos americanos, construyendo dinámicamente URLs basadas en el código UPC/GTIN obtenido de la USDA y apuntando al CDN de Target.

## User Review Required

> [!NOTE]
> Esta estrategia es síncrona y no requiere llamadas adicionales a la red, lo que garantiza una velocidad de carga máxima en la lista de productos.

## Proposed Changes

### Core Data (Utilidades y Mapeo)

#### [NEW] [UsdaImageResolver.kt](file:///D:/Jorgito/Proyects/Mimercado/core/data/src/main/java/com/saico/mimercado/core/data/util/UsdaImageResolver.kt)
- Implementar la lógica de construcción de URL: `https://images.targetimg1.com/wcsstore/TargetSAS/img/p/{subfolder}/{upc}.jpg`.
- Extraer la subcarpeta (dígitos del final del UPC) según el patrón del CDN.

#### [MODIFY] [NetworkProductRepository.kt](file:///D:/Jorgito/Proyects/Mimercado/core/data/src/main/java/com/saico/mimercado/core/data/repository/NetworkProductRepository.kt)
- Utilizar `UsdaImageResolver` durante la transformación de DTO a modelo de Dominio.
- Formatear el nombre del producto (Title Case) para mejorar la legibilidad.

### Core UI (Renderizado)

#### [MODIFY] [ProductRow.kt](file:///D:/Jorgito/Proyects/Mimercado/core/ui/src/main/java/com/saico/mimercado/core/ui/components/ProductRow.kt)
- Configurar `AsyncImage` de Coil para manejar el caché en disco.
- Añadir un placeholder y una imagen de error elegante para los casos donde el CDN no tenga la foto exacta.

## Verification Plan

### Automated Tests
- Ejecutar `gradle assembleDebug`.

### Manual Verification
1. Abrir la app y buscar productos conocidos (ej. "Cheerios", "Coca Cola").
2. Verificar que las imágenes cargan instantáneamente desde el CDN de Target.
3. Confirmar que el desplazamiento por la lista sigue siendo fluido (60fps) gracias a que no hay llamadas extra de red.
