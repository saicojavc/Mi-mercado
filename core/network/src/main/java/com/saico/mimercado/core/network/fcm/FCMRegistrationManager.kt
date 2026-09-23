package com.saico.mimercado.core.network.fcm

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val userRef = firestore.collection("users").document(uid)

                val tokenData = mapOf(
                    "fcmToken" to token,
                    "lastSeen" to System.currentTimeMillis()
                )

                userRef.set(tokenData, SetOptions.merge()).addOnSuccessListener {
                    Log.d("FCMRegistration", "✅ FCM device token updated successfully on users/$uid")
                }.addOnFailureListener { e ->
                    Log.e("FCMRegistration", "❌ Failed to update user registration on Firestore", e)
                }
            } else {
                Log.e("FCMRegistration", "❌ Failed to retrieve FCM token", task.exception)
            }
        }
    }
}
