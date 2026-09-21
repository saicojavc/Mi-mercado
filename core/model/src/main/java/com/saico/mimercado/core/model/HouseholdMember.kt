package com.saico.mimercado.core.model

enum class MemberRole {
    ADULT,
    CHILD
}

data class HouseholdMember(
    val uid: String = "",
    val displayName: String = "",
    val avatarIcon: String? = null,
    val photoUrl: String? = null,
    val role: MemberRole = MemberRole.ADULT
)
