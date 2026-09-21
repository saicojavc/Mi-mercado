package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import javax.inject.Inject

class UpdateMemberAvatarUseCase @Inject constructor(
    private val repository: HouseholdRepository
) {
    suspend operator fun invoke(householdId: String, uid: String, avatarIcon: String): Result<Unit> =
        repository.updateMemberAvatar(householdId, uid, avatarIcon)
}
