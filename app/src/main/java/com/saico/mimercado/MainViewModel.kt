package com.saico.mimercado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.theme.ObserveThemeModeUseCase
import com.saico.mimercado.core.model.ThemeMode
import com.saico.mimercado.core.ui.navigation.routes.Route
import com.saico.mimercado.core.ui.navigation.routes.auth.SignInRoute
import com.saico.mimercado.core.ui.navigation.routes.lists.ShoppingListsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val observeThemeModeUseCase: ObserveThemeModeUseCase
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = observeThemeModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.DARK
        )

    val startDestination: StateFlow<Route?> = authRepository.observeAuthState()
        .map { authUser ->
            if (authUser == null) SignInRoute else ShoppingListsRoute
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
