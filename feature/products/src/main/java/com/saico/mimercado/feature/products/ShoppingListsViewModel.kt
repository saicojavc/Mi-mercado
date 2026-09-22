package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.ObserveUserProfileUseCase
import com.saico.mimercado.core.domain.usecase.household.ObserveHouseholdMembersUseCase
import com.saico.mimercado.core.domain.usecase.lists.ShoppingListUseCases
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.ShoppingListSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingListsUiState(
    val householdId: String = "",
    val lists: List<ShoppingListSummary> = emptyList(),
    val members: List<HouseholdMember> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShoppingListsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val observeHouseholdMembersUseCase: ObserveHouseholdMembersUseCase,
    private val shoppingListUseCases: ShoppingListUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListsUiState(isLoading = true))
    val uiState: StateFlow<ShoppingListsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { authUser ->
                val uid = authUser?.uid ?: ""
                if (uid.isBlank()) {
                    flowOf(ShoppingListsUiState(isLoading = false, error = "Usuario no autenticado"))
                } else {
                    observeUserProfileUseCase(uid).flatMapLatest { userProfile ->
                        val householdId = userProfile?.householdId ?: ""
                        if (householdId.isBlank()) {
                            flowOf(ShoppingListsUiState(isLoading = false, error = "No tienes una familia asignada"))
                        } else {
                            // Launch ensureMainList asynchronously so it never blocks or fails flow subscription
                            viewModelScope.launch {
                                shoppingListUseCases.ensureMainList(householdId, uid)
                            }

                            combine(
                                shoppingListUseCases.observeLists(householdId),
                                observeHouseholdMembersUseCase(householdId)
                            ) { lists, members ->
                                ShoppingListsUiState(
                                    householdId = householdId,
                                    lists = lists,
                                    members = members,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            val householdId = uiState.value.householdId
            if (householdId.isNotBlank()) {
                shoppingListUseCases.deleteList(householdId, listId).onFailure { error ->
                    _uiState.update { it.copy(error = error.message ?: "Error al eliminar lista") }
                }
            }
        }
    }
}
