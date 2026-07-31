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
     * Checks if a raw category from USDA matches a Spanish UI label.
     */
    fun matches(rawCategory: String, uiLabel: String): Boolean {
        if (uiLabel == "Todos") return true
        
        val english = mapToEnglish(uiLabel).lowercase()
        val raw = rawCategory.lowercase()
        
        // Specific checks for common USDA categories
        return when (uiLabel) {
            "Lácteos" -> raw.contains("dairy") || raw.contains("milk") || raw.contains("cheese") || raw.contains("yogurt")
            "Panadería" -> raw.contains("bakery") || raw.contains("bread") || raw.contains("cake")
            "Carnes" -> raw.contains("meat") || raw.contains("beef") || raw.contains("chicken") || raw.contains("pork")
            "Frutas y verduras" -> raw.contains("fruit") || raw.contains("vegetable") || raw.contains("produce")
            "Despensa" -> raw.contains("pantry") || raw.contains("grocery") || raw.contains("snack") || raw.contains("cereal")
            "Limpieza" -> raw.contains("clean") || raw.contains("detergent") || raw.contains("household")
            "Bebidas" -> raw.contains("beverage") || raw.contains("drink") || raw.contains("juice") || raw.contains("soda") || raw.contains("water")
            else -> raw.contains(english) || raw.contains(uiLabel.lowercase())
        }
    }
}
