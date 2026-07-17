const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyOnProductAdded = functions.firestore
  .document("households/{householdId}/cart/{itemId}")
  .onCreate(async (snap, context) => {
    const { householdId, itemId } = context.params;
    const newItem = snap.data();
    const addedBy = newItem.addedBy;

    console.log(`\n========== FUNCTION START ==========`);
    console.log(`📦 Product: ${newItem.nombre}`);
    console.log(`👤 addedBy: ${addedBy}`);
    console.log(`📝 itemId: ${itemId}`);

    try {
      console.log(`🔍 Fetching users from households/${householdId}/users`);
      const usersSnapshot = await admin
        .firestore()
        .collection(`households/${householdId}/users`)
        .get();

      console.log(`👥 Total users: ${usersSnapshot.size}`);

      if (usersSnapshot.size === 0) {
        console.log(`❌ No users found in database!`);
        return;
      }

      const tokensToNotify = [];
      usersSnapshot.docs.forEach((doc) => {
        const docId = doc.id;
        const token = doc.data().deviceToken;
        const isMe = docId === addedBy;

        console.log(`   User: ${docId}`);
        console.log(`      Token exists: ${token ? "YES" : "NO"}`);
        console.log(`      Is me (${addedBy}): ${isMe}`);

        if (!isMe && token) {
          tokensToNotify.push(token);
        }
      });

      console.log(`✉️ Tokens to notify: ${tokensToNotify.length}`);

      if (tokensToNotify.length === 0) {
        console.log(`⚠️ No tokens to send to`);
        return;
      }

      console.log(`📤 Sending message...`);
      let successCount = 0;
      let failureCount = 0;

      for (const token of tokensToNotify) {
        try {
          await admin.messaging().send({
            token: token,
            notification: {
              title: "🛒 Producto agregado",
              body: `${newItem.nombre} ha sido añadido al carrito`,
            },
            android: {
              priority: "high",
              notification: {
                icon: "ic_launcher_foreground",
                color: "#00D9FF",
                sound: "default",
              },
            },
          });
          successCount++;
        } catch (err) {
          failureCount++;
          console.log(`   ❌ Token error: ${err.code}`);
        }
      }

      console.log(`✅ RESULT: ${successCount} success, ${failureCount} failed`);
    } catch (error) {
      console.error(`🚨 ERROR:`, error);
    }
    console.log(`========== FUNCTION END ==========\n`);
  });