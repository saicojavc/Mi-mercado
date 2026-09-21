# Mi Mercado (FamilyCart)

Android app for a family to manage their grocery list collaboratively:
a product catalog (USDA FoodData Central + custom products), a real-time
shared cart, and households formed via invite codes.

> **Note on this README:** it combines what was confirmed through real
> logs/testing during development with what an automated code audit
> reported. Wherever there isn't full certainty, it's flagged explicitly
> — adjust it to match what the code actually has at each point.

---

## Tech stack

- **Kotlin + Jetpack Compose** (Material 3) for the entire UI.
- **Modular architecture** (Clean Architecture, app/core/feature):
  modules confirmed via Gradle: `app`, `feature:auth`, `feature:products`,
  `feature:search`, `feature:cart`, `feature:settings`, `core:model`,
  `core:data`, `core:domain`, `core:ui`, `core:common`, `core:database`,
  `core:datastore`, `core:network`.
- **Hilt** for dependency injection.
- **Navigation Compose** with type-safe routes (serializable objects).
- **Firebase**: Authentication (Google Sign-In via Credential Manager),
  **Cloud Firestore** (not Realtime Database — confirmed), Cloud
  Messaging.
- **Single-activity**, every screen is a Compose destination.

---

## Current functionality

### Authentication and households
- Google login using `androidx.credentials` (Credential Manager) — not
  the classic `GoogleSignInClient` API, which Google no longer
  recommends.
- On first sign-in, a user profile is created automatically **and** a
  household of the user's own, with a 6-character join code.
- A user belongs to a single household at a time. Joining another
  household with its code transfers membership (leaves the old one,
  joins the new one).
- Per-member roles: `ADULT` and `CHILD`. *(Note: for now this is a
  simple permission restriction, not a suggest→approve flow for minors —
  see Roadmap.)*
- Household management (view members, share/regenerate the code, join
  another) lives inside Settings, not as a mandatory screen on app
  launch.
- Avatars: a set of 12 animal icons, selectable by each user.

### Catalog and cart
- Two views: **Habitual** (frequent/favorite products) and **Discover**
  (search against USDA FoodData Central).
- Categorization with English/Spanish name normalization.
- **Custom products**: products that don't exist in USDA can be created;
  they're marked as permanently habitual.
- **Barcode scanning** to look up products by UPC.
- Collaborative cart with real-time sync via Firestore; each item shows
  who added it.

---

## Data model (Cloud Firestore)

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
  householdId   // lookup index for joining by code
```

Current security rules (summary): every collection under a household
requires membership (`isMember()`, checked against `members/{uid}`);
`members` can only be created/deleted for oneself
(`request.auth.uid == uid`); `joinCodes` allows `get` by exact code but
never a full `list`, so all existing codes are never exposed.

**Pending technical debt:** per-device FCM token registration currently
lives under `households/{id}/users`, sharing a collection name with the
Auth profile at the root (`/users/{uid}`) — they're different things with
the same name. Rename it to `fcmTokens` before more code accumulates
around the current name.

---

## Project setup

1. **Firebase**: place your `google-services.json` in `app/`. The
   project uses Cloud Firestore, Authentication (Google provider), and
   Cloud Messaging — enable them in the console.
2. **Release signing**: create a `keystore.properties` file at the root
   (don't commit it) with `storeFile`, `storePassword`, `keyAlias`,
   `keyPassword`, and wire it up in `app/build.gradle.kts` via
   `signingConfigs`.
3. **The SHA-1 of every keystore you use** (debug and release) must be
   registered in Firebase console → Project settings → your Android app
   → SHA certificate fingerprints. **This bit the project once**:
   without the release SHA-1 registered, the Google account picker
   simply doesn't show up in release builds, with no visible exception
   in Logcat.
4. **ProGuard/R8**: `app/proguard-rules.pro` already includes what's
   needed for Firestore (reflection over model classes), Credential
   Manager, and enums — don't add blanket `-keep` rules for entire
   library packages (`androidx.**`, `dagger.**`, etc.); most already ship
   their own consumer rules, and keeping them manually just bloats app
   size.

---

## Roadmap (designed, not implemented)

Documented in detail in separate contract-level plans — if you keep them
in the repo, `docs/plans/` is the convention used to generate them:

- **Family coordination**: a real suggest→approve flow for the `CHILD`
  role, purchase history, habitual reorder suggestions, lists separate
  from the main one (requires introducing a `ShoppingList` entity that
  doesn't exist yet).
- **Avoiding duplicates / efficiency**: pantry inventory, expiration
  dates with reminders, sorting the list by aisle.
- **Health/budget**: full-cart nutrition summary, per-member allergy
  alerts (with the caveat that USDA doesn't have a reliably structured
  allergen field).
- **Offline**: the app currently depends on live Firestore, with no
  local cache (Room) or sync queue — pending an architecture decision
  before more code piles up on the current pattern.
- **UI polish**: a color/typography token system, a unified `ProductCard`
  across screens, standardized image handling, deduplication of repeated
  results in Discover, cart state (fixed summary + empty state), and a
  persistent cart bar with a collapsed-by-default Habitual catalog
  (designed, not built).
- **Automatic photo search** for custom products (Pexels/Unsplash) —
  implementation status unconfirmed.

---

## Lessons learned (so the investigation doesn't get repeated)

- `GoogleApiManager: Failed to get service from broker` in Logcat is
  internal Google Play Services noise reported across dozens of
  unrelated apps — on its own, it isn't a sign that login is broken.
- A Firestore listener that fails on permissions (`PERMISSION_DENIED`)
  can crash the app if the error isn't explicitly caught in the
  callback — don't assume a Firestore exception is always recoverable
  by default.
- FCM/device registration under `households/{id}/users` has nothing to
  do with Auth's `users/{uid}` — same name, different collections (see
  Technical debt above).
