package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.ObserveUserProfileUseCase
import com.saico.mimercado.core.domain.usecase.lists.ShoppingListUseCases
import com.saico.mimercado.core.model.ListCoverTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val defaultListThemes = listOf(
    ListCoverTheme(colorStart = "#0F172A", colorEnd = "#06B6D4", icon = "shopping_cart"),
    ListCoverTheme(colorStart = "#EC4899", colorEnd = "#8B5CF6", icon = "celebration"),
    ListCoverTheme(colorStart = "#10B981", colorEnd = "#059669", icon = "home"),
    ListCoverTheme(colorStart = "#F59E0B", colorEnd = "#D97706", icon = "pets"),
    ListCoverTheme(colorStart = "#3B82F6", colorEnd = "#1D4ED8", icon = "car"),
    ListCoverTheme(colorStart = "#6366F1", colorEnd = "#4338CA", icon = "restaurant")
)

data class CreateListUiState(
    val name: String = "",
    val selectedTheme: ListCoverTheme = defaultListThemes.first(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreateListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val shoppingListUseCases: ShoppingListUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateListUiState())
    val uiState: StateFlow<CreateListUiState> = _uiState.asStateFlow()

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onThemeSelected(theme: ListCoverTheme) {
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    fun createList() {
        val name = uiState.value.name.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "El nombre de la lista no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val authUser = authRepository.observeAuthState().firstOrNull()
            val uid = authUser?.uid ?: ""
            if (uid.isBlank()) {
                _uiState.update { it.copy(isSaving = false, error = "Usuario no autenticado") }
                return@launch
            }

            val userProfile = observeUserProfileUseCase(uid).firstOrNull()
            val householdId = userProfile?.householdId ?: ""
            if (householdId.isBlank()) {
                _uiState.update { it.copy(isSaving = false, error = "No perteneces a ningún hogar") }
                return@launch
            }

            val result = shoppingListUseCases.createList(
                householdId = householdId,
                name = name,
                coverTheme = uiState.value.selectedTheme,
                createdBy = uid
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isSaving = false, error = err.message ?: "Error al crear lista") }
                }
            )
        }
    }
}
