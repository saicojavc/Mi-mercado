package com.saico.mimercado.core.common

object UsdaImageResolver {

    fun getImageUrlFromUpc(gtinUpc: String?): String {
        if (gtinUpc.isNullOrBlank()) return ""
        val cleanUpc = gtinUpc.filter { it.isDigit() }
        if (cleanUpc.length < 8) return ""
        return buildOffUrl(cleanUpc)
    }

    /**
     * Builds the segmented path required by Open Food Facts for 90% of US products.
     * Example: 044000032025 -> 044/000/032/025/front_en.400.jpg
     */
    fun buildOffUrl(upc: String): String {
        val segments = mutableListOf<String>()
        var i = 0
        while (i < upc.length) {
            val end = (i + 3).coerceAtMost(upc.length)
            segments.add(upc.substring(i, end))
            i += 3
        }
        val path = segments.joinToString("/")
        return "https://images.openfoodfacts.org/images/products/$path/front_en.400.jpg"
    }

    /**
     * Creates a high-probability search query for image thumbnails.
     */
    fun getSearchThumbnailUrl(brand: String, name: String): String {
        val query = "$brand $name".replace(" ", "+")
            .replace("&", "")
            .replace(",", "")
        // Using Bing's thumbnail service which is very reliable for product thumbnails
        return "https://tse1.mm.bing.net/th?q=$query&w=200&h=200&c=7&rs=1&p=0&dpr=1&pid=1.7"
    }
}
