package com.saico.mimercado.core.domain.usecase.theme

import com.saico.mimercado.core.domain.repository.ThemePreferencesRepository
import com.saico.mimercado.core.model.ThemeMode
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: ThemePreferencesRepository
) {
    suspend operator fun invoke(mode: ThemeMode): Result<Unit> = repository.setThemeMode(mode)
}
