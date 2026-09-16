package com.saico.mimercado.core.common

object CategoryMapper {
    
    fun mapToEnglish(category: String): String {
        return when (category) {
            "Lácteos" -> "Dairy"
            "Panadería" -> "Bakery"
            "Carnes" -> "Meat"
            "Frutas y verduras" -> "Fruit Vegetable"
            "Despensa" -> "Pantry"
            "Limpieza" -> "Cleaning"
            "Bebidas" -> "Beverage"
            else -> category
        }
    }

    /**
     * Legacy matches function
     */
    fun matches(rawCategory: String, uiLabel: String): Boolean {
        return matchesSmart(rawCategory, "", uiLabel)
    }

    /**
     * Checks if a raw category or product name matches a Spanish UI label smartly.
     */
    fun matchesSmart(rawCategory: String, productName: String, uiLabel: String): Boolean {
        if (uiLabel == "Todos") return true
        if (rawCategory.equals(uiLabel, ignoreCase = true)) return true
        
        val english = mapToEnglish(uiLabel).lowercase()
        val rawCat = rawCategory.lowercase()
        val rawName = productName.lowercase()
        
        // Combine sources for matching
        val combined = "$rawCat $rawName"
        
        return when (uiLabel.lowercase()) {
            "lácteos" -> combined.contains("dairy") || combined.contains("milk") || combined.contains("cheese") || combined.contains("yogurt") || combined.contains("queso") || combined.contains("leche") || combined.contains("cream") || combined.contains("butter")
            "panadería" -> combined.contains("bakery") || combined.contains("bread") || combined.contains("cake") || combined.contains("pan ") || combined.contains("galleta") || combined.contains("cookie") || combined.contains("toast")
            "carnes" -> combined.contains("meat") || combined.contains("beef") || combined.contains("chicken") || combined.contains("pork") || combined.contains("carne") || combined.contains("pollo") || combined.contains("turkey") || combined.contains("bacon")
            "frutas y verduras" -> combined.contains("fruit") || combined.contains("vegetable") || combined.contains("produce") || combined.contains("manzana") || combined.contains("platano") || combined.contains("tomate") || combined.contains("salad")
            "despensa" -> combined.contains("pantry") || combined.contains("grocery") || combined.contains("snack") || combined.contains("cereal") || combined.contains("arroz") || combined.contains("pasta") || combined.contains("bean") || combined.contains("oil")
            "limpieza" -> combined.contains("clean") || combined.contains("detergent") || combined.contains("household") || combined.contains("jabon") || combined.contains("soap")
            "bebidas" -> combined.contains("beverage") || combined.contains("drink") || combined.contains("juice") || combined.contains("soda") || combined.contains("water") || combined.contains("cafe") || combined.contains("agua") || combined.contains("coffee") || combined.contains("tea")
            else -> combined.contains(english) || combined.contains(uiLabel.lowercase())
        }
    }

    /**
     * Normalizes any raw category string into one of our UI categories.
     */
    fun getNormalizedCategory(rawCategory: String, productName: String = ""): String {
        val uiCategories = listOf("Lácteos", "Panadería", "Carnes", "Frutas y verduras", "Despensa", "Limpieza", "Bebidas")
        
        for (uiCategory in uiCategories) {
            if (matchesSmart(rawCategory, productName, uiCategory)) {
                return uiCategory
            }
        }
        
        return "Despensa" // Fallback seguro
    }
}
