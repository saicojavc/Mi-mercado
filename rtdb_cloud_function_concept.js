/**
 * Node.js Firebase Cloud Function Concept for Mi Mercado
 *
 * This Cloud Function monitors the Firebase Realtime Database path 'households/familia_valdes/cart'
 * for any changes (additions, updates, deletions) and sends a Firebase Cloud Messaging (FCM)
 * notification to other household users.
 */

const { onRequest } = require("firebase-functions/v2/https");
const { onValueWritten } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onCartUpdated = onValueWritten(
    {
        ref: "/households/familia_valdes/cart/{productId}",
        region: "us-central1"
    },
    async (event) => {
        const productId = event.params.productId;
        const beforeData = event.data.before.val();
        const afterData = event.data.after.val();

        let title = "Actualización del Carrito";
        let message = "El carrito ha sido actualizado.";
        let senderId = null;

        if (!afterData) {
            // Item was removed
            title = "Producto eliminado";
            message = `Se eliminó "${beforeData.nombre}" del carrito.`;
            senderId = beforeData.addedBy;
        } else if (!beforeData) {
            // Item was added
            title = "Producto agregado";
            message = `Se agregó ${afterData.cantidad}x "${afterData.nombre}" al carrito.`;
            senderId = afterData.addedBy;
        } else if (beforeData.cantidad !== afterData.cantidad) {
            // Quantity changed
            title = "Cantidad actualizada";
            message = `La cantidad de "${afterData.nombre}" cambió de ${beforeData.cantidad} a ${afterData.cantidad}.`;
            senderId = afterData.addedBy;
        } else {
            // No significant change
            return null;
        }

        console.log(`Cart action by user ${senderId}: ${message}`);

        // Fetch all other users in the household to get their FCM tokens
        const usersSnapshot = await admin.database()
            .ref("/households/familia_valdes/users")
            .once("value");

        const tokens = [];
        usersSnapshot.forEach((childSnapshot) => {
            const userId = childSnapshot.key;
            const userData = childSnapshot.val();
            // Don't send notification to the user who triggered the action
            if (userId !== senderId && userData.deviceToken) {
                tokens.push(userData.deviceToken);
            }
        });

        if (tokens.length === 0) {
            console.log("No other active users with device tokens found.");
            return null;
        }

        // Construct FCM multicast message
        const payload = {
            notification: {
                title: title,
                body: message,
            },
            data: {
                click_action: "FLUTTER_NOTIFICATION_CLICK", // or appropriate intent filter action
                productId: productId,
                type: "cart_update"
            },
            tokens: tokens
        };

        // Send messages
        try {
            const response = await admin.messaging().sendEachForMulticast(payload);
            console.log(`Successfully sent ${response.successCount} notifications; ${response.failureCount} failed.`);
        } catch (error) {
            console.error("Error sending multicast message:", error);
        }

        return null;
    }
);
