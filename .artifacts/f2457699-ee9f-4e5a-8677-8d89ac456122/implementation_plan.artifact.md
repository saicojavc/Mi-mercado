# Aesthetic Override Design Plan

Overhaul the "Mi mercado" app UI with a modern, clean, digital-vibrant look, focusing on Inter typography, a cyan-centric color palette, and expressive animations.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///D:/Jorgito/Proyects/Mimercado/gradle/libs.versions.toml)
- Add `ui-text-google-fonts` to the `[libraries]` section to enable Google Fonts integration.

#### [MODIFY] [build.gradle.kts](file:///D:/Jorgito/Proyects/Mimercado/app/build.gradle.kts)
- Add `androidx.compose.ui:ui-text-google-fonts` dependency.

### Design Tokens

#### [MODIFY] [Color.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/theme/Color.kt)
- Define new color palette:
  - `AppBackground = Color(0xFFFAFBFC)`
  - `PrimaryCyan = Color(0xFF00D9FF)`
  - `SecondaryTeal = Color(0xFF06B6D4)`
  - `TextDark = Color(0xFF0F172A)`
  - `NeutralLight = Color(0xFFE2E8F0)`
  - `SuccessGreen = Color(0xFF10B981)`
  - `ErrorRed = Color(0xFFEF4444)`

#### [MODIFY] [Type.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/theme/Type.kt)
- Configure **Inter** font using Google Fonts.
- Define `Display`, `Body`, and `Label` styles with specific weights and spacing to match the modern aesthetic.

#### [MODIFY] [Theme.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/theme/Theme.kt)
- Update `MiMercadoTheme` to utilize the new color palette and typography.
- Set the `surface` and `background` colors to `AppBackground`.
- Disable dynamic color if it conflicts with the specific aesthetic override (or integrate it if possible, but the prompt suggests a specific override).

### UI Components

#### [MODIFY] [ProductRow.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/components/ProductRow.kt)
- Update `Card` with `shape = RoundedCornerShape(12.dp)` and `elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)`.
- Implement background hover/press effects using `InteractionSource`.
- Create a circular 44dp "+" button with `PrimaryCyan` background and white icon.
- Add `scale` animation (0.95f) on press for the add button.

#### [MODIFY] [CategoryFilter.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/components/CategoryFilter.kt)
- Implement custom chips:
  - Inactive: `#E2E8F0` background, `#0F172A` text.
  - Active: `PrimaryCyan` background, white text.
- Add 100ms fade/scale animation on tap using `animateColorAsState` and `animateFloatAsState`.

### Screens

#### [MODIFY] [ProductListScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/ProductListScreen.kt)
- Set `Scaffold` background to `AppBackground`.
- Update `TopAppBar` with the new color palette and `Inter` bold title.
- Reposition cart badge to top-right with `PrimaryCyan` background and white text.
- Implement a bounce animation for the badge when the quantity changes.

#### [MODIFY] [CartScreen.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/screens/CartScreen.kt)
- Apply the 12dp border radius to item cards.
- Use `PrimaryCyan` for primary actions and accents.
- Ensure the list follows the clean, spaced-out layout of the new design.

## Verification Plan

### Automated Tests
- Build the project to ensure dependency resolution and code compilation.
- Run existing unit tests (if any) to ensure no regressions in business logic.

### Manual Verification
- **Colors**: Inspect the UI to ensure light theme colors match the specified hex codes (e.g., Background `#FAFBFC`, Cyan `#00D9FF`).
- **Typography**: Verify that the text renders using the **Inter** font.
- **Animations**:
  - Test the "Add" button scale effect on press.
  - Observe the cart badge bounce when items are added.
  - Verify chip selection fade/scale transitions.
- **Layout**: Confirm `12dp` border radius on cards and consistent spacing throughout the app.
