package com.saico.mimercado.core.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("verified_images_cache", Context.MODE_PRIVATE)

    /**
     * Guarda la URL exitosa para un producto.
     * @param key Puede ser el UPC o el ID del producto.
     */
    fun saveVerifiedUrl(key: String, url: String) {
        if (key.isBlank() || url.isBlank()) return
        prefs.edit().putString(key, url).apply()
    }

    /**
     * Recupera la URL previamente verificada.
     */
    fun getVerifiedUrl(key: String): String? {
        if (key.isBlank()) return null
        return prefs.getString(key, null)
    }
}
