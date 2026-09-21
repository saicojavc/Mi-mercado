# Mi Mercado (FamilyCart)

App de Android para que una familia gestione su lista de mercado de forma
colaborativa: catálogo de productos (USDA FoodData Central + productos
propios), carrito en tiempo real, y hogares formados por código de
invitación.

> **Nota sobre este README:** combina lo confirmado por logs/pruebas reales
> durante el desarrollo con lo reportado por una auditoría automatizada del
> código. Donde no hay certeza total, lo marco explícitamente — ajústalo
> según lo que el código realmente tenga en cada punto.

---

## Stack técnico

- **Kotlin + Jetpack Compose** (Material 3) para toda la UI.
- **Arquitectura modular** (Clean Architecture, app/core/feature):
  módulos confirmados vía Gradle: `app`, `feature:auth`, `feature:products`,
  `feature:search`, `feature:cart`, `feature:settings`, `core:model`,
  `core:data`, `core:domain`, `core:ui`, `core:common`, `core:database`,
  `core:datastore`, `core:network`.
- **Hilt** para inyección de dependencias.
- **Navigation Compose** con rutas type-safe (objetos serializables).
- **Firebase**: Authentication (Google Sign-In vía Credential Manager),
  **Cloud Firestore** (no Realtime Database — confirmado), Cloud Messaging.
- **Single-activity**, todas las pantallas como destinos de Compose.

---

## Funcionalidad actual

### Autenticación y hogares
- Login con Google usando `androidx.credentials` (Credential Manager) —
  no la API `GoogleSignInClient` clásica, que Google dejó de recomendar.
- Al iniciar sesión por primera vez, se crea automáticamente un perfil de
  usuario **y** un hogar propio con un código de unión de 6 caracteres.
- Un usuario pertenece a un solo hogar a la vez. Unirse a otro hogar con
  su código transfiere la membresía (sale del anterior, entra al nuevo).
- Roles por miembro: `ADULT` y `CHILD`. *(Nota: por ahora es una
  restricción simple de permisos, no un flujo de sugerencia→aprobación
  para menores — ver Roadmap.)*
- Gestión de hogar (ver miembros, compartir/regenerar código, unirse a
  otro) vive dentro de Ajustes, no como pantalla obligatoria al abrir la
  app.
- Avatares: set de 12 íconos de animales, elegibles por cada usuario.

### Catálogo y carrito
- Dos vistas: **Habitual** (productos frecuentes/favoritos) y **Discover**
  (búsqueda contra USDA FoodData Central).
- Categorización con normalización de nombres en inglés/español.
- **Productos personalizados**: se pueden crear productos que no existen
  en USDA; quedan marcados como habituales de forma permanente.
- **Escaneo de código de barras** para buscar productos por UPC.
- Carrito colaborativo con sincronización en tiempo real vía Firestore;
  cada item muestra quién lo agregó.

---

## Modelo de datos (Cloud Firestore)

```
users/{uid}
  displayName, email, photoUrl, householdId, createdAt

households/{householdId}
  name, ownerUid, joinCode, createdAt

households/{householdId}/members/{uid}
  displayName, avatarIcon, photoUrl, role (ADULT|CHILD), joinedAt

households/{householdId}/cart/{itemId}
households/{householdId}/favorites/{productId}

joinCodes/{code}
  householdId   // índice de búsqueda para unirse por código
```

Reglas de seguridad actuales (resumen): cada colección bajo un hogar exige
pertenencia (`isMember()`, verificado contra `members/{uid}`); `members`
solo se crea/borra a uno mismo (`request.auth.uid == uid`); `joinCodes`
permite `get` por código exacto pero nunca `list` completo, para no
exponer todos los códigos existentes.

**Deuda técnica pendiente:** el registro de tokens de FCM por dispositivo
vive hoy bajo `households/{id}/users`, con el mismo nombre de colección
que el perfil de Auth en la raíz (`/users/{uid}`) — son cosas distintas
con el mismo nombre. Renombrar a `fcmTokens` antes de que se acumule más
código alrededor del nombre actual.

---

## Configuración del proyecto

1. **Firebase**: coloca tu `google-services.json` en `app/`. El proyecto
   usa Cloud Firestore, Authentication (proveedor Google) y Cloud
   Messaging — habilítalos en la consola.
2. **Firma de release**: crea `keystore.properties` en la raíz (no lo
   subas a git) con `storeFile`, `storePassword`, `keyAlias`,
   `keyPassword`, y conéctalo en `app/build.gradle.kts` vía
   `signingConfigs`.
3. **SHA-1 de cada keystore que uses** (debug y release) debe estar
   registrado en Firebase console → Configuración del proyecto → tu app
   Android → Huellas digitales. **Esto mordió al proyecto una vez**: sin
   el SHA-1 de release registrado, el selector de cuentas de Google
   simplemente no aparece en builds de release, sin ninguna excepción
   visible en Logcat.
4. **ProGuard/R8**: `app/proguard-rules.pro` ya incluye lo necesario para
   Firestore (reflexión sobre modelos), Credential Manager y enums — no
   agregar reglas `-keep` de paquetes enteros de librerías (`androidx.**`,
   `dagger.**`, etc.), la mayoría ya traen sus propias reglas empaquetadas
   y mantenerlas a mano solo infla el tamaño de la app.

---

## Roadmap (diseñado, no implementado)

Documentado en detalle en planes de contrato aparte — si los guardas en
el repo, `docs/plans/` es la convención usada para generarlos:

- **Coordinación familiar**: flujo real de sugerencia/aprobación para el
  rol `CHILD`, historial de compras, recompra habitual, listas separadas
  de la principal (requiere introducir una entidad `ShoppingList` que hoy
  no existe).
- **Anti-duplicados / eficiencia**: inventario de despensa, fechas de
  vencimiento con recordatorio, orden de lista por pasillo.
- **Salud/presupuesto**: resumen nutricional del carrito completo,
  alertas de alergia por miembro (con la salvedad de que USDA no tiene un
  campo de alérgenos estructurado confiable).
- **Offline**: hoy la app depende de Firestore en vivo, sin cache local
  (Room) ni cola de sincronización — pendiente de decisión de
  arquitectura antes de que se acumule más código sobre el patrón actual.
- **Pulido de UI**: sistema de tokens de color/tipografía, `ProductCard`
  unificado entre pantallas, manejo estandarizado de imágenes,
  deduplicación de resultados repetidos en Discover, estado del carrito
  (resumen fijo + empty state), y una barra de carrito persistente con
  catálogo de Habitual colapsable por defecto (diseñado, no construido).
- **Búsqueda automática de foto** para productos personalizados
  (Pexels/Unsplash) — estado de implementación sin confirmar.

---

## Lecciones aprendidas (para no repetir la investigación)

- `GoogleApiManager: Failed to get service from broker` en Logcat es
  ruido interno de Google Play Services reportado en decenas de apps sin
  relación entre sí — no es, por sí solo, señal de que el login esté roto.
- Un listener de Firestore que falla por permisos (`PERMISSION_DENIED`)
  puede tumbar la app si el error no se captura explícitamente en el
  callback — no asumas que una excepción de Firestore es siempre
  recuperable por defecto.
- Los emails de FCM/perfil bajo `households/{id}/users` no tienen
  relación con el `users/{uid}` de Auth — nombre compartido, colecciones
  distintas (ver Deuda técnica arriba).
