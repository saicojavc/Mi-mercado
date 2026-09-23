package com.saico.mimercado.core.domain.usecase.household

import javax.inject.Inject

data class HouseholdUseCases @Inject constructor(
    val createHousehold: CreateHouseholdUseCase,
    val updateHouseholdName: UpdateHouseholdNameUseCase,
    val observeHousehold: ObserveHouseholdUseCase,
    val observeMembers: ObserveHouseholdMembersUseCase,
    val updateMemberRole: UpdateMemberRoleUseCase,
    val updateMemberAvatar: UpdateMemberAvatarUseCase,
    val joinHouseholdByCode: JoinHouseholdByCodeUseCase,
    val regenerateJoinCode: RegenerateJoinCodeUseCase
)
