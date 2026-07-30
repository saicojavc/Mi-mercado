package com.saico.mimercado.core.network.fcm

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.saico.mimercado.core.common.UserProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMRegistrationManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProvider: UserProvider
) {
    fun registerDeviceToken() {
        val userId = userProvider.getUserId()
        val userRef = firestore.collection("households").document("familia_valdes")
            .collection("users").document(userId)
        
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val username = "Usuario " + userId.takeLast(4)
                userRef.set(mapOf(
                    "deviceToken" to token,
                    "lastSeen" to System.currentTimeMillis(),
                    "username" to username
                )).addOnSuccessListener {
                    Log.d("FCMRegistration", "✅ User registration and device token updated successfully on Firestore")
                }.addOnFailureListener { e ->
                    Log.e("FCMRegistration", "❌ Failed to update user registration on Firestore", e)
                }
            } else {
                Log.e("FCMRegistration", "❌ Failed to retrieve FCM token", task.exception)
            }
        }
    }
}
