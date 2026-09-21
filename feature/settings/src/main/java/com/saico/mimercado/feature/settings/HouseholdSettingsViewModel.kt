package com.saico.mimercado.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.AuthUseCases
import com.saico.mimercado.core.domain.usecase.household.HouseholdUseCases
import com.saico.mimercado.core.model.MemberRole
import com.saico.mimercado.feature.settings.model.HouseholdSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseholdSettingsViewModel @Inject constructor(
    private val householdUseCases: HouseholdUseCases,
    private val authUseCases: AuthUseCases,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HouseholdSettingsUiState(isLoading = true))
    val uiState: StateFlow<HouseholdSettingsUiState> = _uiState.asStateFlow()

    private var currentHouseholdId: String? = null
    private var currentUserUid: String? = null

    init {
        loadHouseholdAndMembers()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadHouseholdAndMembers() {
        authRepository.observeAuthState()
            .onEach { authUser ->
                currentUserUid = authUser?.uid
            }
            .mapNotNull { it?.uid }
            .flatMapLatest { uid ->
                authUseCases.observeUserProfile(uid)
            }
            .onEach { profile ->
                currentHouseholdId = profile?.householdId
            }
            .flatMapLatest { profile ->
                val householdId = profile?.householdId
                if (householdId.isNullOrEmpty()) {
                    // Si por algún motivo el perfil no tiene ID de hogar (escenario de error o borrado manual)
                    flowOf(HouseholdSettingsUiState(isLoading = false))
                } else {
                    combine(
                        householdUseCases.observeHousehold(householdId),
                        householdUseCases.observeMembers(householdId)
                    ) { household, members ->
                        val isAdult = members.find { it.uid == currentUserUid }?.role == MemberRole.ADULT
                        HouseholdSettingsUiState(
                            household = household,
                            members = members,
                            isCurrentUserAdult = isAdult,
                            currentUserUid = currentUserUid,
                            isLoading = false
                        )
                    }
                }
            }
            .onEach { newState ->
                _uiState.update { newState }
            }
            .catch { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun createNewHousehold(name: String) {
        val uid = currentUserUid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            householdUseCases.createHousehold(name, uid).fold(
                onSuccess = {
                    // El flow de observeUserProfile detectará el cambio y refrescará la UI
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun onJoinCodeInputChanged(code: String) {
        _uiState.update { it.copy(joinCodeInput = code) }
    }

    fun joinHouseholdWithCode() {
        val code = _uiState.value.joinCodeInput.trim().uppercase()
        val uid = currentUserUid ?: return
        if (code.length != 6) {
            _uiState.update { it.copy(error = "El código debe tener exactamente 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isJoining = true, error = null) }
            householdUseCases.joinHouseholdByCode(code, uid).fold(
                onSuccess = {
                    _uiState.update { it.copy(isJoining = false, joinCodeInput = "") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isJoining = false, error = error.message) }
                }
            )
        }
    }

    fun regenerateJoinCode() {
        val householdId = currentHouseholdId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegeneratingCode = true, error = null) }
            householdUseCases.regenerateJoinCode(householdId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isRegeneratingCode = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isRegeneratingCode = false, error = error.message) }
                }
            )
        }
    }

    fun onRoleChanged(memberUid: String, role: MemberRole) {
        val householdId = currentHouseholdId ?: return
        viewModelScope.launch {
            householdUseCases.updateMemberRole(householdId, memberUid, role)
        }
    }

    fun onAvatarChanged(memberUid: String, avatarIcon: String) {
        val householdId = currentHouseholdId ?: return
        viewModelScope.launch {
            householdUseCases.updateMemberAvatar(householdId, memberUid, avatarIcon)
        }
    }
}
