package com.saico.mimercado.core.datastore

import com.saico.mimercado.core.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemePreferencesRepositoryImplTest {

    @Test
    fun themeModeEnum_containsExpectedValues() {
        assertEquals(3, ThemeMode.values().size)
        assertEquals(ThemeMode.LIGHT, ThemeMode.valueOf("LIGHT"))
        assertEquals(ThemeMode.DARK, ThemeMode.valueOf("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.valueOf("SYSTEM"))
    }
}
