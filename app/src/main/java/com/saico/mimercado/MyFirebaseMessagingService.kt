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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.saico.mimercado.util.SharedPreferencesUtil

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "Refreshed token: $token")
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCMService", "Message received: ${remoteMessage.data}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Mi Mercado"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Nuevo mensaje"

        // Show standard notification in status bar
        sendNotification(title, body)

        // Show Toast if app is in foreground
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "$title: $body", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendTokenToServer(token: String) {
        val userId = SharedPreferencesUtil.getUserId(applicationContext)
        val database = FirebaseDatabase.getInstance("https://when-babe-default-rtdb.firebaseio.com/")
        val userRef = database.getReference("households/familia_valdes/users/$userId")
        userRef.child("fcmToken").setValue(token)
            .addOnSuccessListener {
                Log.d("FCMService", "Token updated successfully on server")
            }
            .addOnFailureListener { e ->
                Log.e("FCMService", "Failed to update token on server", e)
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mi Mercado Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Mi Mercado FCM notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
