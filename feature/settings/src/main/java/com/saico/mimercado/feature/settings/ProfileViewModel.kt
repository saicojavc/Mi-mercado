package com.saico.mimercado.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.AuthUseCases
import com.saico.mimercado.core.domain.usecase.household.HouseholdUseCases
import com.saico.mimercado.core.model.Household
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.MemberRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userDisplayName: String = "Usuario",
    val userEmail: String = "",
    val userAvatar: String? = null,
    val isDarkMode: Boolean = true,
    val household: Household? = null,
    val members: List<HouseholdMember> = emptyList(),
    val isCurrentUserAdult: Boolean = false,
    val currentUserUid: String? = null,
    val joinCodeInput: String = "",
    val isJoining: Boolean = false,
    val isRegeneratingCode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authUseCases: AuthUseCases,
    private val householdUseCases: HouseholdUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentHouseholdId: String? = null
    private var currentUserUid: String? = null

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
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
                    flowOf(
                        ProfileUiState(
                            userDisplayName = profile?.displayName ?: "Usuario",
                            userEmail = profile?.email ?: "",
                            currentUserUid = currentUserUid,
                            isLoading = false
                        )
                    )
                } else {
                    combine(
                        householdUseCases.observeHousehold(householdId),
                        householdUseCases.observeMembers(householdId)
                    ) { household, members ->
                        val currentMember = members.find { it.uid == currentUserUid }
                        val isAdult = currentMember?.role == MemberRole.ADULT
                        ProfileUiState(
                            userDisplayName = profile?.displayName ?: currentMember?.displayName ?: "Usuario",
                            userEmail = profile?.email ?: "",
                            userAvatar = currentMember?.avatarIcon,
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
                _uiState.value = newState
            }
            .catch { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun createNewHousehold(name: String) {
        val uid = currentUserUid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            householdUseCases.createHousehold(name, uid).onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
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

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
