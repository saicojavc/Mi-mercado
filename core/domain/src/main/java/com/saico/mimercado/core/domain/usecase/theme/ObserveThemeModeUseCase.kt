package com.saico.mimercado.core.domain.usecase.theme

import com.saico.mimercado.core.domain.repository.ThemePreferencesRepository
import com.saico.mimercado.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThemeModeUseCase @Inject constructor(
    private val repository: ThemePreferencesRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repository.observeThemeMode()
}
