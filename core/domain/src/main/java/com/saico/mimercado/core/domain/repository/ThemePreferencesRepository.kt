package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemePreferencesRepository {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode): Result<Unit>
}
