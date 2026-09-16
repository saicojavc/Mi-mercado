package com.saico.mimercado.feature.auth

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
