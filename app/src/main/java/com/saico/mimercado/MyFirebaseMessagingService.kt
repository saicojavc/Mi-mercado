package com.saico.mimercado

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.saico.mimercado.core.common.UserProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var userProvider: UserProvider

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "✅ Refreshed token: $token")
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCMService", "✅ Message received: ${remoteMessage.data}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Mi Mercado"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Nuevo mensaje"

        sendNotification(title, body)

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "$title: $body", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendTokenToServer(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(uid).get().addOnSuccessListener { userSnapshot ->
            val householdId = userSnapshot.getString("householdId") ?: "familia_valdes"

            val userRef = firestore.collection("households").document(householdId)
                .collection("users").document(uid)

            userRef.update("deviceToken", token)
                .addOnSuccessListener {
                    Log.d("FCMService", "✅ Token updated in Firestore for household $householdId")
                }
                .addOnFailureListener { e ->
                    Log.d("FCMService", "⚠️ Update failed, creating or merging document")
                    userRef.set(mapOf(
                        "deviceToken" to token,
                        "lastSeen" to System.currentTimeMillis()
                    ), SetOptions.merge())
                }
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "mi_mercado_fcm_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mi Mercado Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Mi Mercado FCM notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
