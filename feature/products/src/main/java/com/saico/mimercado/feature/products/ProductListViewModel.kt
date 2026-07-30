package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.cart.CartRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {
    val categories = listOf("Todos", "Lácteos", "Panadería", "Carnes", "Frutas y verduras", "Despensa", "Limpieza", "Bebidas")
    
    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _filteredProducts = MutableStateFlow(Product.sampleProducts)
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        _filteredProducts.value = if (category == "Todos") {
            Product.sampleProducts
        } else {
            Product.sampleProducts.filter { it.categoria == category }
        }
    }

    fun navigateToCart() {
        navigator.navigate(NavigationCommand.NavigateTo(CartRoute))
    }
}
