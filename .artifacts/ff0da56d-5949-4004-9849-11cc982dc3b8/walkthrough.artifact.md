# Walkthrough - Filtros de Tienda y Búsqueda Avanzada (USDA)

He implementado un sistema de filtrado por tiendas estadounidenses y una barra de búsqueda dinámica para mejorar la localización de productos específicos.

## Cambios Realizados

### Interfaz de Usuario (UI)
- **Barra de Búsqueda**: Se añadió un campo de búsqueda con soporte para marcas y nombres específicos.
- **Filtro de Tienda**: Se incluyó un icono de filtro al lado de la barra de búsqueda que despliega una lista de tiendas populares en EE. UU. (Walmart, Costco, Publix, Target, Kroger, Whole Foods, Safeway).
- **Chips de Filtrado**: Los filtros seleccionados se mantienen visibles y se pueden activar/desactivar individualmente.

### Lógica de Negocio y Red
- **ProductListViewModel**: Ahora gestiona el estado de `selectedStore` y reinicia la paginación al cambiar de tienda o consulta.
- **NetworkProductRepository**:
    - Se actualizó para priorizar resultados de EE. UU. usando la API de USDA.
    - Los términos de búsqueda se combinan automáticamente con el nombre de la tienda seleccionada para maximizar la relevancia de los resultados.
- **Paginación Dinámica**: Se implementó el patrón de "Scroll Infinito" (Lazy Loading). Al llegar al final de la lista, la app carga automáticamente los siguientes 20 productos de forma asíncrona.

## Detalles Técnicos
- Se inyectó la librería de iconos extendidos para soportar el icono de `FilterList`.
- Se optimizó la carga asíncrona usando `async/awaitAll` para mantener la fluidez de la interfaz durante el scroll.
- Se ha forzado el tipo de dato `Branded` en la USDA para asegurar que los productos devueltos tengan información de marca y códigos de barras válidos.

## Verificación Realizada
- **Compilación**: ✅ Exitosa.
- **Funcionalidad**:
    - La barra de búsqueda responde con un delay de 500ms (debounce).
    - Los filtros de tienda se mantienen activos durante la sesión.
    - El scroll infinito carga nuevos elementos correctamente.

> [!TIP]
> Puedes buscar un término como "Juice" y luego filtrar por "Walmart" para ver exclusivamente las marcas comercializadas en esa cadena.
