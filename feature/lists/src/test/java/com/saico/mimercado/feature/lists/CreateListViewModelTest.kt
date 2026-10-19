package com.saico.mimercado.feature.lists

import com.saico.mimercado.core.model.ListCoverTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CreateListViewModelTest {

    private lateinit var uiState: CreateListUiState

    @Before
    fun setUp() {
        uiState = CreateListUiState()
    }

    @Test
    fun initialState_hasDefaultThemeAndEmptyName() {
        assertEquals("", uiState.name)
        assertEquals("#0F172A", uiState.selectedTheme.colorStart)
        assertNull(uiState.error)
    }

    @Test
    fun nameChange_updatesUiState() {
        val updatedState = uiState.copy(name = "Fiesta de Cumpleaños")
        assertEquals("Fiesta de Cumpleaños", updatedState.name)
    }

    @Test
    fun themeSelection_updatesUiState() {
        val newTheme = ListCoverTheme(colorStart = "#EC4899", colorEnd = "#8B5CF6", icon = "celebration")
        val updatedState = uiState.copy(selectedTheme = newTheme)
        assertEquals("#EC4899", updatedState.selectedTheme.colorStart)
        assertEquals("celebration", updatedState.selectedTheme.icon)
    }
}
