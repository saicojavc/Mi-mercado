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
        
        firestore.collection("users").document(uid).get().addOnSuccessListener { userSnapshot ->
            val householdId = userSnapshot.getString("householdId") ?: "familia_valdes"
            
            val userRef = firestore.collection("households").document(householdId)
                .collection("users").document(uid)
            
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    val username = userSnapshot.getString("displayName") ?: ("Usuario " + uid.takeLast(4))
                    
                    val initialData = mapOf(
                        "deviceToken" to token,
                        "lastSeen" to System.currentTimeMillis(),
                        "username" to username,
                        "avatarIcon" to "fox_blue",
                        "role" to "ADULT"
                    )

                    userRef.set(initialData, SetOptions.merge()).addOnSuccessListener {
                        Log.d("FCMRegistration", "✅ User registration and device token updated successfully on Firestore under household $householdId")
                    }.addOnFailureListener { e ->
                        Log.e("FCMRegistration", "❌ Failed to update user registration on Firestore", e)
                    }
                } else {
                    Log.e("FCMRegistration", "❌ Failed to retrieve FCM token", task.exception)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FCMRegistration", "❌ Failed to fetch user profile for householdId", e)
        }
    }
}
