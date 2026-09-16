package com.saico.mimercado.core.model

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    REVOKED,
    EXPIRED
}

data class HouseholdInvitation(
    val id: String = "",
    val householdId: String = "",
    val invitedEmail: String = "",
    val invitedBy: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING
)
