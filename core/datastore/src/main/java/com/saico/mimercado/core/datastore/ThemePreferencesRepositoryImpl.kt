package com.saico.mimercado.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saico.mimercado.core.domain.repository.ThemePreferencesRepository
import com.saico.mimercado.core.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

@Singleton
class ThemePreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemePreferencesRepository {

    private val themeKey = stringPreferencesKey("theme_mode")

    override fun observeThemeMode(): Flow<ThemeMode> {
        return context.themeDataStore.data.map { preferences ->
            val modeName = preferences[themeKey] ?: ThemeMode.DARK.name
            try {
                ThemeMode.valueOf(modeName)
            } catch (e: Exception) {
                ThemeMode.DARK
            }
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> = try {
        context.themeDataStore.edit { preferences ->
            preferences[themeKey] = mode.name
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
