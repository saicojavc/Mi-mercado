package com.saico.mimercado

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.saico.mimercado.core.ui.navigation.routes.Route
import com.saico.mimercado.core.ui.navigation.routes.products.ProductsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: State<Boolean> = _isDarkMode

    val firstScreen: Route = ProductsRoute
}
