const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyOnProductAdded = functions.firestore
  .document("households/{householdId}/cart/{itemId}")
  .onCreate(async (snap, context) => {
    const {householdId} = context.params;
    const newItem = snap.data();
    const addedBy = newItem.addedBy;

    console.log(`Nuevo producto: ${newItem.nombre}`);

    try {
      const usersSnapshot = await admin
        .firestore()
        .collection(`households/${householdId}/users`)
        .get();

      const tokensToNotify = usersSnapshot.docs
        .filter((doc) => doc.id !== addedBy)
        .map((doc) => doc.data().deviceToken)
        .filter((token) => token);

      if (tokensToNotify.length === 0) {
        return;
      }

      const message = {
        notification: {
          title: "Producto agregado",
          body: `${newItem.nombre} anadido al carrito`,
        },
        android: {
          notification: {
            color: "#00D9FF",
            sound: "default",
          },
        },
        tokens: tokensToNotify,
      };

      await admin.messaging().sendMulticast(message);
    } catch (error) {
      console.error("Error:", error);
    }
  });