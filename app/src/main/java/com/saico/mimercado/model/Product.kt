package com.saico.mimercado.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val imagenUrl: String = ""
) {
    companion object {
        val sampleProducts = listOf(
            Product("1", "Leche Entera", "Lácteos", "https://images.unsplash.com/photo-1563636619-e9107da5a76a?q=80&w=200&auto=format&fit=crop"),
            Product("2", "Yogurt Natural", "Lácteos", "https://images.unsplash.com/photo-1571212215596-4e56bf3aa9d7?q=80&w=200&auto=format&fit=crop"),
            Product("3", "Pan Integral", "Panadería", "https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=200&auto=format&fit=crop"),
            Product("4", "Croissant", "Panadería", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?q=80&w=200&auto=format&fit=crop"),
            Product("5", "Bistec de Res", "Carnes", "https://images.unsplash.com/photo-1603048588665-791ca8aea617?q=80&w=200&auto=format&fit=crop"),
            Product("6", "Pechuga de Pollo", "Carnes", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?q=80&w=200&auto=format&fit=crop"),
            Product("7", "Manzana Roja", "Frutas y verduras", "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?q=80&w=200&auto=format&fit=crop"),
            Product("8", "Plátano", "Frutas y verduras", "https://images.unsplash.com/photo-1571771894821-ad9b5886b45d?q=80&w=200&auto=format&fit=crop"),
            Product("9", "Arroz Extra", "Despensa", "https://images.unsplash.com/photo-1586201375761-83865001e31c?q=80&w=200&auto=format&fit=crop"),
            Product("10", "Aceite de Oliva", "Despensa", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?q=80&w=200&auto=format&fit=crop"),
            Product("11", "Detergente Líquido", "Limpieza", "https://images.unsplash.com/photo-1610557892470-55d9e80c0bce?q=80&w=200&auto=format&fit=crop"),
            Product("12", "Jabón de Platos", "Limpieza", "https://images.unsplash.com/photo-1622060859473-b3c9597b864d?q=80&w=200&auto=format&fit=crop"),
            Product("13", "Agua Mineral", "Bebidas", "https://images.unsplash.com/photo-1560023907-5f339617ea30?q=80&w=200&auto=format&fit=crop"),
            Product("14", "Jugo de Naranja", "Bebidas", "https://images.unsplash.com/photo-1613478223719-2ab802602423?q=80&w=200&auto=format&fit=crop")
        )
    }
}
