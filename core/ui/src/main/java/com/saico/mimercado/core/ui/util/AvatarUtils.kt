package com.saico.mimercado.core.ui.util

data class Avatar(
    val id: String,
    val emoji: String,
    val label: String
)

object AvatarUtils {
    val avatars = listOf(
        Avatar("fox", "🦊", "Zorro"),
        Avatar("bear", "🐻", "Oso"),
        Avatar("panda", "🐼", "Panda"),
        Avatar("koala", "🐨", "Koala"),
        Avatar("lion", "🦁", "León"),
        Avatar("tiger", "🐯", "Tigre"),
        Avatar("rabbit", "🐰", "Conejo"),
        Avatar("cat", "🐱", "Gato"),
        Avatar("dog", "🐶", "Perro"),
        Avatar("monkey", "🐵", "Mono"),
        Avatar("frog", "🐸", "Rana"),
        Avatar("pig", "🐷", "Cerdito")
    )

    fun getAvatarEmoji(id: String?): String = avatars.find { it.id == id }?.emoji ?: "👤"
}
