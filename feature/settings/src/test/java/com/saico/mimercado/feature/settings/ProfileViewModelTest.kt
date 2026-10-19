package com.saico.mimercado.feature.settings

import com.saico.mimercado.core.model.Household
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.MemberRole
import com.saico.mimercado.core.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileViewModelTest {

    @Test
    fun initialState_defaultsToUsuarioAndDarkTheme() {
        val state = ProfileUiState()
        assertEquals("Usuario", state.userDisplayName)
        assertEquals("", state.userEmail)
        assertNull(state.userAvatar)
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertNull(state.household)
        assertTrue(state.members.isEmpty())
        assertFalse(state.isCurrentUserAdult)
        assertNull(state.toastMessage)
    }

    @Test
    fun themeMode_updatesInUiState() {
        val state = ProfileUiState().copy(themeMode = ThemeMode.LIGHT)
        assertEquals(ThemeMode.LIGHT, state.themeMode)
    }

    @Test
    fun household_holdsNameAndJoinCode() {
        val household = Household(id = "h100", name = "Los García", joinCode = "ABC123", ownerUid = "u1")
        val state = ProfileUiState().copy(household = household)
        assertEquals("Los García", state.household?.name)
        assertEquals("ABC123", state.household?.joinCode)
    }

    @Test
    fun membersRoleCheck_determinesAdultStatus() {
        val members = listOf(
            HouseholdMember(uid = "u1", displayName = "Jorge", role = MemberRole.ADULT),
            HouseholdMember(uid = "u2", displayName = "Santiago", role = MemberRole.CHILD)
        )
        val state = ProfileUiState(
            currentUserUid = "u1",
            members = members,
            isCurrentUserAdult = true
        )
        assertTrue(state.isCurrentUserAdult)
        assertEquals(2, state.members.size)
    }
}
