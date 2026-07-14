package com.saico.mimercado.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val emoji: String = ""
) {
    companion object {
        val sampleProducts = listOf(
            // Lácteos
            Product("1", "Queso", "Lácteos", "🧀"),
            Product("2", "Leche", "Lácteos", "🥛"),
            Product("3", "Yogurt", "Lácteos", "🍦"),
            Product("4", "Mantequilla", "Lácteos", "🧈"),
            Product("5", "Huevos", "Lácteos", "🥚"),

            // Panadería
            Product("6", "Pan", "Panadería", "🍞"),
            Product("7", "Tortillas", "Panadería", "🫓"),
            Product("8", "Pan de molde", "Panadería", "🍞"),

            // Carnes
            Product("9", "Pollo", "Carnes", "🍗"),
            Product("10", "Carne de res", "Carnes", "🥩"),
            Product("11", "Carne de cerdo", "Carnes", "🥓"),
            Product("12", "Jamón", "Carnes", "🍖"),
            Product("13", "Salchichas", "Carnes", "🌭"),

            // Frutas y verduras
            Product("14", "Tomate", "Frutas y verduras", "🍅"),
            Product("15", "Cebolla", "Frutas y verduras", "🧅"),
            Product("16", "Papa", "Frutas y verduras", "🥔"),
            Product("17", "Aguacate", "Frutas y verduras", "🥑"),
            Product("18", "Plátano", "Frutas y verduras", "🍌"),
            Product("19", "Manzana", "Frutas y verduras", "🍎"),
            Product("20", "Limón", "Frutas y verduras", "🍋"),
            Product("21", "Ajo", "Frutas y verduras", "🧄"),

            // Despensa
            Product("22", "Arroz", "Despensa", "🍚"),
            Product("23", "Frijoles", "Despensa", "🫘"),
            Product("24", "Aceite", "Despensa", "🍾"),
            Product("25", "Azúcar", "Despensa", "🧂"),
            Product("26", "Sal", "Despensa", "🧂"),
            Product("27", "Café", "Despensa", "☕"),
            Product("28", "Harina", "Despensa", "🌾"),
            Product("29", "Pasta", "Despensa", "🍝"),

            // Limpieza
            Product("30", "Detergente", "Limpieza", "🧼"),
            Product("31", "Papel higiénico", "Limpieza", "🧻"),
            Product("32", "Jabón", "Limpieza", "🧼"),
            Product("33", "Cloro", "Limpieza", "🧪"),
            Product("34", "Servilletas", "Limpieza", "🧻"),

            // Bebidas
            Product("35", "Agua", "Bebidas", "💧"),
            Product("36", "Refresco", "Bebidas", "🥤"),
            Product("37", "Jugo", "Bebidas", "🧃"),
            Product("38", "Cerveza", "Bebidas", "🍺")
        )
    }

}
