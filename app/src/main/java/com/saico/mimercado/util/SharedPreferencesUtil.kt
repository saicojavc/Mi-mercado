package com.saico.mimercado.util

import android.content.Context
import java.util.UUID

object SharedPreferencesUtil {
    private const val PREFS_NAME = "mi_mercado_prefs"
    private const val KEY_USER_ID = "user_id"

    fun getUserId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId.isNullOrEmpty()) {
            userId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
        }
        return userId
    }
}
