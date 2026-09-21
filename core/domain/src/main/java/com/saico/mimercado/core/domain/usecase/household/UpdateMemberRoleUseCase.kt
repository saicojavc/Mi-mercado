package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import com.saico.mimercado.core.model.MemberRole
import javax.inject.Inject

class UpdateMemberRoleUseCase @Inject constructor(
    private val repository: HouseholdRepository
) {
    suspend operator fun invoke(householdId: String, uid: String, role: MemberRole): Result<Unit> =
        repository.updateMemberRole(householdId, uid, role)
}
