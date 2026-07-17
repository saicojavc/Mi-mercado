package com.saico.mimercado

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.saico.mimercado.ui.screens.ProductListScreen
import com.saico.mimercado.ui.theme.MiMercadoTheme
import com.saico.mimercado.util.SharedPreferencesUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Dynamic permission check on Android 13+
        if (Build.VERSION.SDK_INT >= 33) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("MainActivity", "✅ Notification permission granted")
                } else {
                    Log.w("MainActivity", "❌ Notification permission denied")
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize device token registration on startup
        val userId = SharedPreferencesUtil.getUserId(this)
        val firestore = FirebaseFirestore.getInstance()
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
                    Log.d("MainActivity", "✅ User registration and device token updated successfully on Firestore")
                }.addOnFailureListener { e ->
                    Log.e("MainActivity", "❌ Failed to update user registration on Firestore", e)
                }
            } else {
                Log.e("MainActivity", "❌ Failed to retrieve FCM token on startup", task.exception)
            }
        }

        setContent {
            MiMercadoTheme {
                ProductListScreen()
            }
        }
    }
}
