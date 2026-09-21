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
        val cleanUpc = upc.padStart(12, '0') // Ensure standard length
        val segments = mutableListOf<String>()
        var i = 0
        while (i < cleanUpc.length) {
            val end = (i + 3).coerceAtMost(cleanUpc.length)
            segments.add(cleanUpc.substring(i, end))
            i += 3
        }
        val path = segments.joinToString("/")
        return "https://images.openfoodfacts.org/images/products/$path/front_en.400.jpg"
    }

    /**
     * Builds a Walmart-style image URL from UPC.
     */
    fun buildWalmartUrl(upc: String): String {
        val cleanUpc = upc.filter { it.isDigit() }.padStart(12, '0').takeLast(12)
        return "https://i5.walmartimages.com/asr/$cleanUpc.jpg"
    }

    /**
     * Builds a Target-style image URL from UPC.
     */
    fun buildTargetUrl(upc: String): String {
        val cleanUpc = upc.filter { it.isDigit() }.padStart(12, '0').takeLast(12)
        return "https://target.scene7.com/is/image/Target/GUEST_$cleanUpc?wid=400&hei=400&fmt=pjpeg"
    }

    /**
     * Creates a high-probability search query for image thumbnails.
     */
    fun getSearchThumbnailUrl(brand: String, name: String): String {
        val query = "$brand $name".replace(" ", "+")
            .replace("&", "")
            .replace(",", "")
            .replace("++", "+")
            .trim('+')
        // Bing thumbnail service
        return "https://tse1.mm.bing.net/th?q=$query&w=400&h=400&c=7&rs=1&p=0&dpr=1&pid=1.7"
    }

    /**
     * Google-based fallback search (via a public proxy or common pattern if available)
     * For now, we'll just refine the Bing search with different parameters as a 4th fallback.
     */
    fun getSecondarySearchUrl(brand: String, name: String): String {
        val query = name.replace(" ", "+").trim('+')
        return "https://tse1.mm.bing.net/th?q=$query&w=400&h=400&c=7&rs=1&p=0&dpr=1&pid=1.7"
    }
}
