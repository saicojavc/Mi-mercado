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
            Product("3", "Nata", "Lácteos", "🍶"),
            Product("4", "Yogurt", "Lácteos", "🍦"),
            Product("5", "Mantequilla", "Lácteos", "🧈"),
            Product("6", "Huevos", "Lácteos", "🥚"),

            // Panadería
            Product("7", "Pan", "Panadería", "🍞"),
            Product("8", "Tortillas", "Panadería", "🫓"),
            Product("9", "Croissant", "Panadería", "🥐"),
            // Carnes
            Product("10", "Pollo", "Carnes", "🍗"),
            Product("11", "Carne de res", "Carnes", "🥩"),
            Product("12", "Carne de cerdo", "Carnes", "🥓"),
            Product("13", "Carne de cordero", "Carnes", "🥩"),
            Product("14", "Bacon", "Carnes", "🥓"),
            Product("15", "Jamón", "Carnes", "🍖"),
            Product("16", "Salchichas", "Carnes", "🌭"),

            // Frutas y verduras
            Product("17", "Tomate", "Frutas y verduras", "🍅"),
            Product("18", "Cebolla", "Frutas y verduras", "🧅"),
            Product("19", "Papa", "Frutas y verduras", "🥔"),
            Product("20", "Aguacate", "Frutas y verduras", "🥑"),
            Product("21", "Plátano", "Frutas y verduras", "🍌"),
            Product("22", "Manzana", "Frutas y verduras", "🍎"),
            Product("23", "Limón", "Frutas y verduras", "🍋"),
            Product("24", "Ajo", "Frutas y verduras", "🧄"),
            Product("25", "Malanga", "Frutas y verduras", "🫚"),
            Product("26", "Boniato", "Frutas y verduras", "🍠"),
            Product("27", "Mandarina", "Frutas y verduras", "🍑"),
            Product("28", "Uva", "Frutas y verduras", "🍇"),
            Product("29", "Fresa", "Frutas y verduras", "🍓"),
            Product("30", "Zanahoria", "Frutas y verduras", "🥕"),
            Product("31", "Maiz", "Frutas y verduras", "🌽"),
            Product("32", "Berenjena", "Frutas y verduras", "🍆"),
            Product("33", "Pimiento", "Frutas y verduras", "🫑"),
            Product("34", "Champiñón", "Frutas y verduras", "🍄"),
            Product("35", "Lechuga", "Frutas y verduras", "🥬"),
            Product("36", "Brócoli", "Frutas y verduras", "🥦"),
            Product("37", "Kiwi", "Frutas y verduras", "🥝"),
            Product("38", "Melón", "Frutas y verduras", "🍈"),



            // Despensa
            Product("39", "Arroz", "Despensa", "🍚"),
            Product("40", "Frijoles", "Despensa", "🫘"),
            Product("41", "Aceite de Oliva", "Despensa", "🍾"),
            Product("42", "Aceite", "Despensa", "🍾"),
            Product("43", "Azúcar", "Despensa", "🧂"),
            Product("44", "Sal", "Despensa", "🧂"),
            Product("45", "Café", "Despensa", "☕"),
            Product("46", "Harina", "Despensa", "🌾"),
            Product("47", "Pasta", "Despensa", "🍝"),
            Product("48", "Salsa de tomate", "Despensa", "🥫"),

            // Limpieza
            Product("49", "Detergente", "Limpieza", "🧼"),
            Product("50", "Papel higiénico", "Limpieza", "🧻"),
            Product("51", "Jabón", "Limpieza", "🧼"),
            Product("52", "Cloro", "Limpieza", "🧪"),
            Product("53", "Servilletas", "Limpieza", "🧻"),

            // Bebidas
            Product("54", "Agua", "Bebidas", "💧"),
            Product("55", "Refresco", "Bebidas", "🥤"),
            Product("56", "Jugo", "Bebidas", "🧃"),
            Product("57", "Cerveza", "Bebidas", "🍺")
        )
    }

}
