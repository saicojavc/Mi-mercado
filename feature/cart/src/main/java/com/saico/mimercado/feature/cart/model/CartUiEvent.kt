package com.saico.mimercado.feature.cart.model

sealed interface CartUiEvent {
    data class ShowMessage(val message: String) : CartUiEvent
}
