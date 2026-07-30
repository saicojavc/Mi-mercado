package com.saico.mimercado.core.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "mi_mercado_prefs"
        private const val KEY_USER_ID = "user_id"
    }

    fun getUserId(): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId.isNullOrEmpty()) {
            userId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
        }
        return userId
    }
}
