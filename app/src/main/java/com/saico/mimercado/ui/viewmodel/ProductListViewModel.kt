package com.saico.mimercado.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.saico.mimercado.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimpleProductListViewModel : ViewModel() {
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
}
