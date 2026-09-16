# =====================================================================
# Mi Mercado — proguard-rules.pro
# Filosofía: NO mantener paquetes enteros de librerías (androidx, dagger,
# kotlin, kotlinx, firebase) — todas esas ya traen sus propias reglas
# empaquetadas en el AAR. Mantener esos paquetes a mano solo bloquea el
# encogimiento que buscas. Estas reglas cubren únicamente lo que R8 no
# puede deducir por sí solo: tus propios modelos leídos por reflexión.
# =====================================================================

# ---------------------------------------------------------------------
# Firestore: tus modelos de datos (UserProfile, Household, HouseholdMember,
# Product, CartItem, etc.) se deserializan con toObject() usando reflexión
# sobre el constructor sin argumentos y los nombres de campo. R8 no sabe
# que esto pasa, así que sin esta regla puede renombrar los campos y
# Firestore te devuelve objetos con todo en null, en silencio, sin crash.
# ---------------------------------------------------------------------
-keepclassmembers class com.saico.mimercado.core.model.** {
    <init>();
    *;
}

# Si usas la anotación @PropertyName en algún campo (nombre distinto en
# Firestore vs. Kotlin), esto la protege específicamente.
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}

# Necesario para que Firestore pueda inspeccionar tipos genéricos
# (ej. List<Product> dentro de un modelo) via reflexión.
-keepattributes Signature
-keepattributes *Annotation*

# ---------------------------------------------------------------------
# Enums usados como campos de Firestore (MemberRole, CartItemStatus, etc.)
# — Firestore necesita valueOf()/name() intactos para mapear el string
# guardado de vuelta al enum.
# ---------------------------------------------------------------------
-keepclassmembers enum com.saico.mimercado.core.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---------------------------------------------------------------------
# Credential Manager / Google Identity (login con Google).
# Es una librería relativamente nueva; sus consumer-rules deberían
# bastar, pero el flujo de login usando reflexión sobre el Bundle de la
# credencial (GoogleIdTokenCredential.createFrom) es exactamente el tipo
# de cosa que falla en silencio con R8 sin lanzar excepción clara.
# ---------------------------------------------------------------------
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }

# ---------------------------------------------------------------------
# ViewModels con @HiltViewModel — normalmente cubierto por las
# consumer-rules de hilt-navigation-compose.
# ---------------------------------------------------------------------
# -keep class * extends androidx.lifecycle.ViewModel { <init>(...); }