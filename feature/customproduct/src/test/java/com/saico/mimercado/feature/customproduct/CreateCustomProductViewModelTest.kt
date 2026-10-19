package com.saico.mimercado.feature.customproduct

import com.saico.mimercado.core.domain.repository.ImageProvider
import com.saico.mimercado.core.domain.repository.ProductImageResult
import com.saico.mimercado.feature.customproduct.model.CreateCustomProductUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateCustomProductViewModelTest {

    @Test
    fun initialState_defaultsToDespensaCategoryAndEmptyFields() {
        val state = CreateCustomProductUiState()
        assertEquals("", state.name)
        assertEquals("Despensa", state.category)
        assertFalse(state.isEditing)
        assertNull(state.selectedImageUrl)
        assertTrue(state.imageResults.isEmpty())
        assertFalse(state.isSearchingImages)
        assertFalse(state.isSaving)
        assertFalse(state.isSuccess)
        assertNull(state.error)
    }

    @Test
    fun nameAndCategory_updateInUiState() {
        val state = CreateCustomProductUiState().copy(
            name = "Queso de Cabra",
            category = "Lácteos"
        )
        assertEquals("Queso de Cabra", state.name)
        assertEquals("Lácteos", state.category)
    }

    @Test
    fun imageSelection_updatesSelectedImageUrl() {
        val sampleUrl = "https://images.pexels.com/photos/12345/cheese.jpg"
        val state = CreateCustomProductUiState().copy(selectedImageUrl = sampleUrl)
        assertEquals(sampleUrl, state.selectedImageUrl)
    }

    @Test
    fun imageResults_holdsSearchPhotos() {
        val photo = ProductImageResult(
            id = "10",
            thumbnailUrl = "https://thumb.jpg",
            fullUrl = "https://full.jpg",
            photographerName = "John Doe",
            photographerUrl = "https://pexels.com/@john",
            source = ImageProvider.PEXELS
        )
        val state = CreateCustomProductUiState().copy(imageResults = listOf(photo))
        assertEquals(1, state.imageResults.size)
        assertEquals("https://thumb.jpg", state.imageResults.first().thumbnailUrl)
    }
}
