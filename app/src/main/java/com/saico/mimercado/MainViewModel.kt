package com.saico.mimercado

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isDarkMode = mutableStateOf(true) // Dark theme default
    val isDarkMode: State<Boolean> = _isDarkMode

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
