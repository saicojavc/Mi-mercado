# Notification System for Shared Cart

This plan outlines the implementation of a notification system that alerts users when items are added, updated, or removed from the shared cart by other devices.

## User Review Required

> [!IMPORTANT]
> The app will require the `POST_NOTIFICATIONS` permission on Android 13 (API 33) and above. The user will be prompted to grant this permission.

## Proposed Changes

### Core Model

#### [MODIFY] [CartItem.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/model/CartItem.kt)
- Add `senderId: String` field to track which device made the change.

### View Model Logic

#### [MODIFY] [CartViewModel.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/ui/viewmodel/CartViewModel.kt)
- Initialize a `deviceId` using `ANDROID_ID`.
- Switch from `ValueEventListener` to `ChildEventListener` for granular change detection.
- Implement logic to compare `senderId` and trigger notifications for remote changes.
- Update `addToCart` and `removeFromCart` to include the `deviceId`.

### Notification Infrastructure

#### [NEW] [NotificationHelper.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/util/NotificationHelper.kt)
- Utility class to create notification channels and show notifications.

#### [NEW] [MiMercadoApp.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MiMercadoApp.kt)
- Custom `Application` class to initialize the notification channel.

#### [MODIFY] [MainActivity.kt](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/java/com/saico/mimercado/MainActivity.kt)
- Handle `POST_NOTIFICATIONS` permission request.

#### [MODIFY] [AndroidManifest.xml](file:///D:/Jorgito/Proyects/Mimercado/app/src/main/AndroidManifest.xml)
- Register the custom `Application` class.
- Add `POST_NOTIFICATIONS` permission.

## Verification Plan

### Manual Verification
- Run the app on two different emulators/devices.
- Add an item on Device A.
- Verify Device B receives a notification.
- Remove an item on Device A.
- Verify Device B receives a notification.
