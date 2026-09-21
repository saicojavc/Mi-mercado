package com.saico.mimercado.core.model

data class AuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?
)
