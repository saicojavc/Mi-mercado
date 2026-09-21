package com.saico.mimercado.feature.settings.model

import com.saico.mimercado.core.model.Household
import com.saico.mimercado.core.model.HouseholdMember

data class HouseholdSettingsUiState(
    val household: Household? = null,
    val members: List<HouseholdMember> = emptyList(),
    val isCurrentUserAdult: Boolean = false,
    val joinCodeInput: String = "",
    val isJoining: Boolean = false,
    val isRegeneratingCode: Boolean = false,
    val isLoading: Boolean = false,
    val currentUserUid: String? = null,
    val error: String? = null
)
